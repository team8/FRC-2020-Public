package com.palyrobotics.frc2019.util.commands;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.palyrobotics.frc2019.behavior.RoutineManager;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorMeasureSpeedAtOutputRoutine;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.configv2.Configs;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;

public class CommandReceiver {

    private static final int PORT = 5808;

    private static ObjectMapper sMapper = new ObjectMapper();

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
        Subparser run = subparsers.addParser("run");
        run.addArgument("routine_name").setDefault("measure_elevator_speed");
        run.addArgument("parameters").nargs("*");
        subparsers.addParser("save").addArgument("save_config_name");
        subparsers.addParser("calibrate").addSubparsers().dest("subsystem")
                .addParser("arm").help("Resets the Spark encoder so it is in-line with the potentiometer");
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
            String result = executeCommand(command);
            System.out.println(String.format("Result: %s", result));
            mResult.setAndNotify(result);
        });
    }

    public String executeCommand(String command) {
        if (command == null) throw new IllegalArgumentException("Command can not be null!");
        String result;
        // TODO process command and get result
        try {
            Namespace parse = mParser.parseArgs(command.trim().split("\\s+"));
            result = handleParsedCommand(parse);
        } catch (ArgumentParserException parseException) {
            result = parseException.getMessage();
        }
        return result;
    }

    private String handleParsedCommand(Namespace parse) throws ArgumentParserException {
//        System.out.println(parse);
        // TODO less nesting >:( refactor into functions
        String commandName = parse.getString("command");
        switch (commandName) {
            case "get":
            case "set":
            case "save": {
                String configName = parse.getString("config_name");
                try {
                    Class<?> configClass = Configs.getClassFromName(configName);
                    if (configClass == null) throw new ClassNotFoundException();
                    Object configObject = Configs.getRaw(configClass);
                    String fieldName = parse.getString("config_field");
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
                                        String stringValue = parse.getString("config_value");
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
                                    return String.format("File system error saving config %s - this should NOT happen!", configName);
                                }
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
                String routineName = parse.getString("routine_name");
                switch (routineName) {
                    case "measure_elevator_speed": {
                        try {
                            double percentOutput = Double.parseDouble(parse.<String>getList("parameters").get(0));
                            RoutineManager.getInstance().addNewRoutine(new ElevatorMeasureSpeedAtOutputRoutine(percentOutput, Configs.get(ElevatorConfig.class).ff, -10));
                            return String.format("Starting measure elevator routine with percent output %f", percentOutput);
                        } catch (Exception exception) {
                            throw new ArgumentParserException("Could not parse parameters", exception, mParser);
                        }
                    }
                    default: {
                        throw new ArgumentParserException("Routine not recognized!", mParser);
                    }
                }
            }
            case "calibrate": {
                HardwareAdapter.getInstance().getIntake().calibrateIntakeEncoderWithPotentiometer();
                return "Calibrated intake!";
            }
            default: {
                throw new RuntimeException();
            }
        }
    }
}
