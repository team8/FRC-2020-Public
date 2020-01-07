package com.palyrobotics.frc2020.util.config;


import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.VisibilityChecker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Configuration storage using JSON
 *
 * @author Quintin Dwight
 */
public class Configs {

    private static final String CONFIG_FOLDER_NAME = "config";
    private static final Path CONFIG_FOLDER = (RobotBase.isReal()
            ? Paths.get(Filesystem.getDeployDirectory().toString(), CONFIG_FOLDER_NAME)
            : Paths.get(Filesystem.getOperatingDirectory().toString(), "src", "main", "deploy", CONFIG_FOLDER_NAME)).toAbsolutePath();
    private static final HashMap<String, Class<? extends AbstractConfig>> sNameToClass = new HashMap<>();
    private static final HashMap<Class<? extends AbstractConfig>, AbstractConfig> sConfigMap = new HashMap<>(16);
    private static final HashMap<Class<? extends AbstractConfig>, List<Runnable>> sListeners = new HashMap<>();
    private static ObjectMapper sMapper = new ObjectMapper();
    private static final Thread sModifiedListener = new Thread(Configs::watchService), sRobotThread = Thread.currentThread();

    static {
        // Allows us to serialize private fields
        sMapper.setVisibilityChecker(new VisibilityChecker.Std(Visibility.ANY, Visibility.ANY, Visibility.ANY, Visibility.ANY, Visibility.ANY));
    }

    static {
        sModifiedListener.start();
    }

    public static ObjectMapper getMapper() {
        return sMapper;
    }

    /**
     * Retrieve the singleton for this given config class.
     *
     * @param configClass Class of the config.
     * @param <T>         Type of the config class. This is usually inferred from the class argument.
     * @return Singleton or null if not found / registered.
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractConfig> T get(Class<T> configClass) {
        T config = (T) sConfigMap.get(configClass);
        if (config == null) {
            config = read(configClass);
            sConfigMap.put(configClass, config);
            sNameToClass.put(configClass.getSimpleName(), configClass);
        }
        return config;
    }

    public static <T extends AbstractConfig> T get(Class<T> configClass, String name) {
        Path custom = resolveConfigPath(name);
        try {
            T value = sMapper.readValue(custom.toFile(), configClass);
            value.onPostUpdate();
            return value;
        } catch (IOException parseException) {
            throw handleParseError(parseException, configClass);
        }
    }

    /**
     * Listen for changes in a configuration file. An on-changed event is fired once when this function is invoked for initial setup.
     *
     * @param configClass Class of the config.
     * @param onChanged   Listener which consumes new config instance.
     * @param <T>         Type of the config class. This is usually inferred from the class argument.
     */
    public static <T extends AbstractConfig> void listen(Class<T> configClass, Consumer<T> onChanged) {
        onChanged.accept(get(configClass));
        var consumers = sListeners.computeIfAbsent(configClass, newValue -> new ArrayList<>(1));
        consumers.add(() -> onChanged.accept(get(configClass))); // TODO kinda whack
    }

    public static <T extends AbstractConfig> boolean save(Class<T> configClass) {
        try {
            saveOrThrow(configClass);
            return true;
        } catch (IOException saveException) {
            saveException.printStackTrace();
            return false;
        }
    }

    public static void saveOrThrow(Class<? extends AbstractConfig> configClass) throws IOException {
        writeConfig(sConfigMap.get(configClass));
    }

    public static Class<? extends AbstractConfig> getClassFromName(String name) {
        return sNameToClass.get(name);
    }

    public static void set(AbstractConfig config, Object object, Field field, Object value) throws IllegalAccessException {
        field.set(object, value);
        notifyUpdated(config.getClass());
    }

    /**
     * If different, replace the working specified config with the one on the filesystem.
     * This also updates listeners.
     *
     * @param configClass Class of the config.
     * @return Whether or not the config was updated.
     */
    public static boolean reload(Class<? extends AbstractConfig> configClass) {
        AbstractConfig existing = sConfigMap.get(configClass), onFile = read(configClass);
        try {
            if (existing == null || !sMapper.writeValueAsString(existing).equals(sMapper.writeValueAsString(onFile))) {
                sConfigMap.put(configClass, read(configClass));
                notifyUpdated(configClass);
                return true;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method to use the {@link #sMapper} of this class to easily produce a JSON string.
     * This handles errors internally.
     *
     * @param object Any arbitrary object to try and write to JSON.
     * @return The object in JSON format if possible or else "Invalid"
     */
    public static String toJson(Object object) {
        try {
            return sMapper.defaultPrettyPrintingWriter().writeValueAsString(object);
        } catch (IOException formatException) {
            formatException.printStackTrace();
            return "Invalid";
        }
    }

    public static List<String> getActiveConfigNames() {
        return sConfigMap.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    /**
     * Copy an object by converting it to its JSON representation then reads it into a new object.
     * This is slow and creates garbage, so it should not be used often.
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(T toCopy) {
        try {
            return (T) sMapper.readValue(sMapper.writeValueAsString(toCopy), toCopy.getClass());
        } catch (IOException exception) {
            throw new IllegalArgumentException("Type supplied cannot be represented as JSON! Can not copy.", exception);
        }
    }

    /**
     * This should be started in a new thread to watch changes for the folder containing the JSON configuration files.
     * It detects when files are modified and written in the filesystem, then reloads them calling {@link #notifyUpdated(Class)}.
     */
    private static void watchService() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            CONFIG_FOLDER.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!Thread.interrupted()) {
                try {
                    WatchKey key = watcher.take(); // Blocks until an event
                    /* Since there are two times when the listener is notified when a file is saved,
                     * once for the actual content and another time for the timestamp updated,
                     * sleeping will capture both into the same poll event list.
                     * From there, we can filter out what we have already seen to avoid updating more than once. */
                    Thread.sleep(100);
                    List<String> alreadySeen = new ArrayList<>();
                    /* We are on a different thread, so we must be careful updating variables on the main thread.
                     * Current model is to force the robot thread to wait for a notify once we are done updating variables from our thread. */
                    synchronized (sRobotThread) {
                        sRobotThread.wait(100); // In case something goes horribly wrong we can resume robot thread execution
                        for (WatchEvent<?> pollEvent : key.pollEvents()) {
                            if (pollEvent.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> event = (WatchEvent<Path>) pollEvent;
                            Path context = event.context();
                            String configName = context.getFileName().toString().replace(".json", "");
                            Class<? extends AbstractConfig> configClass = sNameToClass.get(configName);
                            if (configClass != null) {
                                if (alreadySeen.contains(configName)) continue;
                                System.out.printf("Config named %s hot reloaded%n", configName);
                                try {
                                    AbstractConfig config = get(configClass);
                                    sMapper.updatingReader(config).readValue(getFileForConfig(configClass).toFile());
                                    config.onPostUpdate();
                                } catch (IOException readException) {
                                    handleParseError(readException, configClass).printStackTrace();
                                    System.err.printf("Error updating config for %s. Aborting reload.%n", configName);
                                }
                                notifyUpdated(configClass);
                                alreadySeen.add(configName);
                            } else {
                                System.err.printf("Unknown file %s%n", context);
                            }
                        }
                        if (!key.reset()) {
                            break;
                        }
                        sRobotThread.notify();
                    }
                } catch (InterruptedException ignored) {
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void notifyUpdated(Class<? extends AbstractConfig> configClass) {
        // TODO nasty
        Optional.ofNullable(sListeners.get(configClass)).ifPresent(listeners -> listeners.forEach(Runnable::run));
    }

    /**
     * Read the given config from the filesystem. There must be a file and it must be valid mappable JSON,
     * desired behavior is to crash if else. In attempt to help the user when there is an invalid JSON file,
     * a default empty class of the same type is printed to console to show desired format (helpful for debugging).
     *
     * @param configClass Class of the config.
     * @param <T>         Type of the config. Must extend {@link AbstractConfig}.
     * @return Instance of given type.
     * @throws RuntimeException when the file cannot be found or it could not be parsed. This is considered a critical error.
     */
    private static <T extends AbstractConfig> T read(Class<T> configClass) {
        Path configFile = getFileForConfig(configClass);
        String configClassName = configClass.getSimpleName();
        if (!Files.exists(configFile)) {
            String errorMessage = String.format(
                    "A config file was not found for %s. Critical error, aborting.%n%n%s%n",
                    configClassName, getDefaultJson(configClass)
            );
            throw new RuntimeException(errorMessage);
        }
        try {
            T value = sMapper.readValue(configFile.toFile(), configClass);
            value.onPostUpdate();
            return value;
        } catch (IOException readException) {
            RuntimeException exception = handleParseError(readException, configClass);
            exception.printStackTrace();
            throw exception;
        }
    }

    private static String getDefaultJson(Class<? extends AbstractConfig> configClass) {
        try {
            return String.format("See here for a default JSON file:%n%s%n",
                    sMapper.defaultPrettyPrintingWriter().writeValueAsString(configClass.getConstructor().newInstance()));
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException exception) {
            System.err.println("Could not show default JSON representation. Something is wrong with the config class definition.");
            exception.printStackTrace();
            return "Invalid";
        }
    }

    private static RuntimeException handleParseError(IOException readException, Class<? extends AbstractConfig> configClass) {
        String errorMessage = String.format(
                "An error occurred trying to read config for class %s%n%nSee here for default JSON: %s%n",
                configClass.getSimpleName(), getDefaultJson(configClass)
        );
        return new RuntimeException(errorMessage, readException);
    }

    private static Path resolveConfigPath(String name) {
        return CONFIG_FOLDER.resolve(String.format("%s.json", name));
    }

    private static Path getFileForConfig(Class<? extends AbstractConfig> configClass) {
        return resolveConfigPath(configClass.getSimpleName());
    }

    private static <T extends AbstractConfig> void writeConfig(T newConfig) throws IOException {
        Path file = getFileForConfig(newConfig.getClass());
        // Creates all folders leading up to target files's parent folder
        Files.createDirectories(file.getParent().getParent());
        sMapper.defaultPrettyPrintingWriter().writeValue(file.toFile(), newConfig);
    }
}
