package com.palyrobotics.frc2019.util.configv2;

import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.config.configv2.ServiceConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Config storage using JSON
 * <p>
 * Register classes with {@link #sConfigs} and get the working copy via {@link #get(Class)}
 *
 * @author Quintin Dwight
 */
public class Configs {

    interface SetPidValue {
        CANError set(CANPIDController controller, double value, int slot);
    }

    private static final Path CONFIG_FOLDER = RobotBase.isReal()
            ? Paths.get(Filesystem.getDeployDirectory().toString(), "config_v2")
            : Paths.get(Filesystem.getOperatingDirectory().toString(), "src", "main", "deploy", "config_v2");

    private static Class[] sConfigs = new Class[]{ElevatorConfig.class, ServiceConfig.class};

    private static ObjectMapper sMapper = new ObjectMapper();
    private static Map<String, Class<?>> sNameToClass = new HashMap<>(sConfigs.length);

    private static Map<Class<?>, Object> sConfigMap = new HashMap<>(sConfigs.length) {{
        for (Class<?> configClass : sConfigs) {
            put(configClass, readGenericConfig(configClass));
            sNameToClass.put(configClass.getSimpleName(), configClass);
        }
    }};

    private static Map<String, SetPidValue> sConfigNameToControllerSetter = Map.ofEntries(
            Map.entry("p", CANPIDController::setP),
            Map.entry("i", CANPIDController::setI),
            Map.entry("d", CANPIDController::setP),
            Map.entry("f", CANPIDController::setFF),
            Map.entry("a", CANPIDController::setSmartMotionMaxAccel),
            Map.entry("v", CANPIDController::setSmartMotionMaxVelocity)
    );

    /**
     * Retrieve the singleton for this given config class.
     * It must be registered with {@link #sConfigs}.
     *
     * @param configClass Class of the config.
     * @param <T>         Type of the config class. This is usually inferred from the class argument.
     * @return Singleton or null if not found / registered.
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractConfig> T get(Class<T> configClass) {
        return (T) sConfigMap.get(configClass);
    }

    public static Object getRaw(Class<?> configClass) {
        return sConfigMap.get(configClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractConfig> boolean save(Class<T> configClass) {
        T config = (T) sConfigMap.get(configClass);
        try {
            writeConfig(config);
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void saveRaw(Class<?> configClass) throws IOException {
        writeConfig(sConfigMap.get(configClass));
    }

    public static void setRaw(Object config, Field field, Object value) throws IllegalAccessException {
        System.out.println("Setting value!");
        field.set(config, value);
        // TODO refactor into better architecture
        String fieldName = field.getName();
        if (sConfigNameToControllerSetter.containsKey(fieldName) && value instanceof Double) {
            CANPIDController controller = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getPIDController();
            double doubleValue = (double) value;
            sConfigNameToControllerSetter.get(fieldName).set(controller, doubleValue, 1);
            System.out.printf("Set %s to %f%n", fieldName, doubleValue);
        }
    }

    public static Class<?> getClassFromName(String name) {
        return sNameToClass.get(name);
    }

    private static File getFileForConfig(Class<?> configClass) {
        return Paths.get(CONFIG_FOLDER.toString(), String.format("%s.json", configClass.getSimpleName())).toFile();
    }

    private static <T> T readGenericConfig(Class<T> configClass) {
        File configFile = getFileForConfig(configClass);
        String configClassName = configClass.getSimpleName();
        if (!configFile.exists()) {
            System.err.printf("A default config file was not found for %s. Writing defaults...%n", configClassName);
            return saveDefaultConfig(configClass);
        }
        try {
            return sMapper.readValue(configFile, configClass);
        } catch (IOException readException) {
            System.err.printf("An error occurred trying to read config for class %s%n", configClassName);
            readException.printStackTrace();
            return saveDefaultConfig(configClass);
        }
    }

    private static <T> T saveDefaultConfig(Class<T> configClass) {
        try {
            T newConfig = configClass.getDeclaredConstructor().newInstance();
            try {
                writeConfig(newConfig);
                System.out.printf("Wrote defaults for %s%n", configClass.getSimpleName());
            } catch (IOException writeDefaultsException) {
                System.err.println("Error writing defaults - this should not happen!");
                writeDefaultsException.printStackTrace();
            }
            return newConfig;
        } catch (Exception createBlankException) {
            System.err.printf("Fatal error, could not create blank config for class %s. Is this a legit non-abstract class?%n", configClass.getSimpleName());
            createBlankException.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static void writeConfig(Object newConfig) throws IOException {
        File file = getFileForConfig(newConfig.getClass()), parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) throw new IOException();
        }
        sMapper.defaultPrettyPrintingWriter().writeValue(file, newConfig);
    }
}
