package com.palyrobotics.frc2020.util.commands;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palyrobotics.frc2020.util.config.ConfigBase;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.service.RobotService;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandReceiver implements RobotService {

	private static final int PORT = 5808;

	private static ObjectMapper sMapper = Configs.getMapper();

	private final ArgumentParser mParser;
	private Server mServer;
	private AtomicString mResult = new AtomicString(), mCommand = new AtomicString();

	public CommandReceiver() {
		mParser = ArgumentParsers.newFor("rio-terminal").build();
		Subparsers subparsers = mParser.addSubparsers().dest("command");
		Subparser set = subparsers.addParser("set");
		set.addArgument("config_name");
		set.addArgument("config_field");
		set.addArgument("config_value");
		Subparser get = subparsers.addParser("get");
		get.addArgument("config_name");
		get.addArgument("config_field").nargs("?"); // "?" means this is optional, and will default to null if not supplied
		get.addArgument("--raw").action(Arguments.storeTrue());
		subparsers.addParser("reload").addArgument("config_name");
		Subparser run = subparsers.addParser("run");
		run.addArgument("routine_name").setDefault("measure_elevator_speed");
		run.addArgument("parameters").nargs("*");
		subparsers.addParser("save").addArgument("config_name");
		subparsers.addParser("calibrate").addSubparsers().dest("subsystem").addParser("arm").help(
				"Resets the Spark encoder so it is " + "in-line with the " + "potentiometer");
	}

	@Override
	public void start() {
		mServer = new Server();
		mServer.getKryo().setRegistrationRequired(false);
		try {
			mServer.addListener(new Listener() {

				@Override
				public void connected(Connection connection) {
					Log.info(getConfigName(), "Command receiver connected!");
				}

				@Override
				public void disconnected(Connection connection) {
					Log.error(getConfigName(), "Command receiver disconnected!");
				}

				@Override
				public void received(Connection connection, Object message) {
					if (message instanceof String) {
						var command = (String) message;
						mCommand.set(command);
						try {
							String result = mResult.waitAndGet();
							mServer.sendToTCP(connection.getID(), result);
						} catch (InterruptedException interruptedException) {
							mServer.close();
							Thread.currentThread().interrupt();
						}
					}
				}
			});
			mServer.start();
			mServer.bind(PORT);
			Log.info(getConfigName(), "Started command receiver server");
		} catch (IOException exception) {
			Log.error(getConfigName(), "Failed to start server", exception);
		}
	}

	@Override
	public void update() {
		mCommand.tryGetAndReset(command -> {
			if (command == null)
				return;
			String result = executeCommand(command);
			mResult.setAndNotify(result);
		});
	}

	public String executeCommand(String command) {
		if (command == null)
			throw new IllegalArgumentException("Command can not be null!");
		String result;
		try {
			Namespace parse = mParser.parseArgs(command.trim().split("\\s+"));
			result = handleParsedCommand(parse);
		} catch (ArgumentParserException parseException) {
			var help = new StringWriter();
			var printer = new PrintWriter(help);
			parseException.getParser().printHelp(printer);
			result = String.format("Error running command: %s%n%s", parseException.getMessage(), help.toString());
		}
		return result;
	}

	private String handleParsedCommand(Namespace parse) throws ArgumentParserException {
		// TODO less nesting >:( refactor into functions
		var commandName = parse.getString("command");
		switch (commandName) {
			case "get":
			case "set":
			case "save":
			case "reload": {
				String configName = parse.getString("config_name");
				if (commandName.equals("get") && configName.equals("Configs")) {
					return String.join(",", Configs.getActiveConfigNames());
				}
				try {
					Class<? extends ConfigBase> configClass = Configs.getClassFromName(configName);
					if (configClass == null)
						throw new ClassNotFoundException();
					ConfigBase configObject = Configs.get(configClass);
					var allFieldNames = parse.getString("config_field");
					try {
						switch (commandName) {
							case "set":
							case "get": {
								String[] fieldNames = allFieldNames == null ? null : allFieldNames.split("\\.");
								Object fieldValue = configObject, fieldParentValue = null;
								Field field = null;
								if (fieldNames != null && fieldNames.length != 0) {
									for (String fieldName : fieldNames) {
										field = getField(field == null ? configClass : field.getType(), fieldName);
										fieldParentValue = fieldValue;
										fieldValue = field.get(fieldValue);
									}
								}
								switch (commandName) {
									case "get": {
										String display = Configs.toJson(fieldValue);
										return parse.getBoolean("raw") ? display : String.format("[%s] %s: %s", configName,
																								 allFieldNames == null ? "all"
																													   : allFieldNames,
																								 display
										);
									}
									case "set": {
										if (field == null)
											return "Can't set entire config file yet!";
										String stringValue = parse.getString("config_value");
										if (stringValue == null)
											return "Must provide a value to set!";
										try {
											Object newFieldValue = sMapper.readValue(stringValue, field.getType());
											Configs.set(configObject, fieldParentValue, field, newFieldValue);
											return String.format("Set field %s on config %s to %s", allFieldNames, configName,
																 stringValue);
										} catch (IOException parseException) {
											return String.format("Error parsing %s for field %s on config %s", stringValue, allFieldNames,
																 configName
											);
										}
									}
									default: {
										throw new RuntimeException();
									}
								}
							}
							case "save": {
								try {
									Configs.saveOrThrow(configClass);
									return String.format("Saved config for %s", configName);
								} catch (IOException saveException) {
									var errorMessage = String.format("File system error saving config %s - this should NOT happen!",
																	 configName
									);
									Log.error(getConfigName(), errorMessage, saveException);
									return errorMessage;
								}
							}
							case "reload": {
								boolean didReload = Configs.reload(configClass);
								return String.format(didReload ? "Reloaded config %s" : "Did not reload config %s", configName);
							}
							default: {
								throw new RuntimeException();
							}
						}
					} catch (NoSuchFieldException noFieldException) {
						return String.format("Error getting field %s, it does not exist!", allFieldNames);
					} catch (IllegalAccessException | IllegalArgumentException illegalAccessException) {
						var errorMessage = String.format("Error setting field %s", allFieldNames);
						Log.warn(getConfigName(), errorMessage, illegalAccessException);
						return errorMessage;
					}
				} catch (ClassNotFoundException configException) {
					return String.format("Unknown config class %s", configName);
				}
			}
			case "run": {
				String routineName = parse.getString("routine_name");
				switch (routineName) {
					default: {
						throw new ArgumentParserException("Routine not recognized!", mParser);
					}
				}
			}

			default: {
				throw new RuntimeException();
			}
		}
	}

	/**
	 * Allows us to get fields that belong to super-classes as well
	 */
	private Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
		var fields = new HashMap<String, Field>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			fields.putAll(Arrays.stream(c.getDeclaredFields()).collect(Collectors.toMap(Field::getName, Function.identity())));
		}
		return Optional.ofNullable(fields.get(name)).orElseThrow(NoSuchFieldException::new);
	}

	@Override
	public String getConfigName() {
		return "commandReceiver";
	}

	public void stop() {
		mServer.stop();
	}
}
