package com.palyrobotics.frc2019.util.commands;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.palyrobotics.frc2019.behavior.RoutineManager;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorMeasureSpeedAtOutputRoutine;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.util.configv2.Configs;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;

public class CommandReceiver {

    private static final int PORT = 5808;

    private final ArgumentParser m_Parser;
    private static ObjectMapper sMapper = new ObjectMapper();

    private Server mServer;
    private AtomicString mResult = new AtomicString(), mCommand = new AtomicString();

    public CommandReceiver() {
        m_Parser = ArgumentParsers.newFor("rio-terminal").build();
        m_Parser.addArgument("command").choices("set", "get", "save", "run");
        m_Parser.addArgument("config");
        m_Parser.addArgument("field").nargs("?");
        m_Parser.addArgument("value").nargs("?").type(String.class);
    }

    public void start() {
        mServer = new Server();
        mServer.getKryo().setRegistrationRequired(false);
        try {
            mServer.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    System.out.println("Connected!");
                }

                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Disconnected!");
                }

                @Override
                public void received(Connection connection, Object message) {
                    if (message instanceof String) {
                        String command = (String) message;
                        mCommand.set(command);
                        try {
                            String result = mResult.waitAndGet();
                            mServer.sendToTCP(connection.getID(), result);
                        } catch (InterruptedException interruptedException) {
                            mServer.close();
                        }
                    }
                }
            });
            mServer.bind(PORT);
            mServer.start();
            System.out.println("Started command receiver server");
        } catch (IOException | IllegalMonitorStateException exception) {
            exception.printStackTrace();
        }
    }

    public void stop() {
        mServer.stop();
    }

    public void update() {
        mCommand.tryGetAndReset(command -> {
            if (command == null) return;
            String result;
            // TODO process command and get result
            try {
                final var parse = m_Parser.parseArgs(command.trim().split("\\s+"));
                System.out.println(parse.toString());
                result = handleConfigCommand(parse);
            } catch (ArgumentParserException parseException) {
                result = parseException.getMessage();
            }
            System.out.println(String.format("Result: %s", result));
            mResult.setAndNotify(result);
        });
    }

    private String handleConfigCommand(Namespace parse) {
        // TODO less nesting >:(
        String configName = parse.getString("config");
        String commandName = parse.getString("command");
        switch (commandName) {
            case "get":
            case "set":
            case "save": {
                try {
                    Class<?> configClass = Configs.getClassFromName(configName);
                    if (configClass == null) throw new ClassNotFoundException();
                    Object configObject = Configs.getRaw(configClass);
                    String fieldName = parse.getString("field");
                    try {
                        switch (commandName) {
                            case "set":
                            case "get": {
                                Field field = fieldName == null ? null : configClass.getDeclaredField(fieldName);
                                switch (commandName) {
                                    case "get": {
                                        if (field == null) {
                                            try {
                                                return sMapper.defaultPrettyPrintingWriter().writeValueAsString(configObject);
                                            } catch (IOException formatConfigException) {
                                                formatConfigException.printStackTrace();
                                                return "Error getting JSON file. This should not happen!";
                                            }
                                        } else {
                                            return String.format("[%s] %s: %s", configName, fieldName, field.get(configObject));
                                        }
                                    }
                                    case "set": {
                                        if (field == null) return "Can't set entire config file yet!";
                                        String stringValue = parse.getString("value");
                                        if (stringValue == null) return "Must provide a value to set!";
                                        try {
                                            Configs.setRaw(configObject, field, sMapper.readValue(stringValue, field.getType()));
                                            return String.format("Set field %s on config %s to %s", fieldName, configName, stringValue);
                                        } catch (IOException parseException) {
                                            return String.format("Error parsing %s for field %s on config %s", stringValue, fieldName, configName);
                                        }
                                    }
                                    default: {
                                        throw new RuntimeException();
                                    }
                                }
                            }
                            case "save": {
                                try {
                                    Configs.saveRaw(configClass);
                                    return String.format("Saved config for %s", configName);
                                } catch (IOException saveException) {
                                    saveException.printStackTrace();
                                    return String.format("Error saving config %s - this should NOT happen!", configName);
                                }
                            }
                            case "run": {
                            }
                            default: {
                                throw new RuntimeException();
                            }
                        }
                    } catch (NoSuchFieldException noFieldException) {
                        return String.format("Error getting field %s, it does not exist!", fieldName);
                    } catch (IllegalAccessException illegalAccessException) {
                        return String.format("Error setting field %s", fieldName);
                    }
                } catch (ClassNotFoundException configException) {
                    return String.format("Unknown config class %s", configName);
                }
            }
            case "run": {
                double po = Double.parseDouble(configName);
                RoutineManager.getInstance().addNewRoutine(new ElevatorMeasureSpeedAtOutputRoutine(po, Configs.get(ElevatorConfig.class).ff, -10));
                return String.format("Starting at %f", po);
            }
            default: {
                throw new RuntimeException();
            }
        }
    }
}
