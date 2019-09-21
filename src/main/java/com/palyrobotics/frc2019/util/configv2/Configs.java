package com.palyrobotics.frc2019.util.configv2;

import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.config.configv2.IntakeConfig;
import com.palyrobotics.frc2019.config.configv2.ServiceConfig;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Configuration storage using JSON
 * <p>
 * Register classes with {@link #sConfigs} and get the working copy via {@link #get(Class)}
 *
 * @author Quintin Dwight
 */
public class Configs {

    /* ============================================================================================================= */

    /**
     * Register classes to be JSON configurable here. This will automatically try to load them from the deploy folder.
     */
    private static final List<Class<? extends AbstractConfig>> sConfigs = List.of(
            ElevatorConfig.class, IntakeConfig.class, ServiceConfig.class
    );

    /* ============================================================================================================= */

    private static ObjectMapper sMapper = new ObjectMapper();

    static {
        // Allows us to serialize private fields
        sMapper.setVisibilityChecker(new VisibilityChecker.Std(Visibility.ANY, Visibility.ANY, Visibility.ANY, Visibility.ANY, Visibility.ANY));
    }

    public static ObjectMapper getMapper() {
        return sMapper;
    }

    private static final String CONFIG_FOLDER_NAME = "config_v2";
    private static final Path CONFIG_FOLDER = (RobotBase.isReal()
            ? Paths.get(Filesystem.getDeployDirectory().toString(), CONFIG_FOLDER_NAME)
            : Paths.get(Filesystem.getOperatingDirectory().toString(), "src", "main", "deploy", CONFIG_FOLDER_NAME)).toAbsolutePath();

    private static final ConcurrentHashMap<String, Class<? extends AbstractConfig>> sNameToClass = new ConcurrentHashMap<>(sConfigs.size());
    private static final ConcurrentHashMap<Class<? extends AbstractConfig>, AbstractConfig> sConfigMap = new ConcurrentHashMap<>(sConfigs.size());
    private static final ConcurrentHashMap<Class<? extends AbstractConfig>, List<Runnable>> sListeners = new ConcurrentHashMap<>();
    private static final Thread sModifiedListener = new Thread(Configs::watchService);

    static {
        sModifiedListener.start();
    }

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
        T config = (T) sConfigMap.get(configClass);
        if (config == null) {
            config = read(configClass);
            sConfigMap.put(configClass, config);
            sNameToClass.put(configClass.getSimpleName(), configClass);
        }
        return config;
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
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void saveOrThrow(Class<? extends AbstractConfig> configClass) throws IOException {
        writeConfig(sConfigMap.get(configClass));
    }

    public static Class<? extends AbstractConfig> getClassFromName(String name) {
        return sNameToClass.get(name);
    }

    public static void set(AbstractConfig config, Field field, Object value) throws IllegalAccessException {
        field.set(config, value);
        notifyUpdated(config.getClass());
    }

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

    public static void toJson(Object object) {
        try {
            System.out.println(sMapper.defaultPrettyPrintingWriter().writeValueAsString(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This should be started in a new thread to watch changes for the folder containing the JSON configuration files.
     * It detects when files are modified and written to on disk, then reloads them calling {@link #notifyUpdated(Class)}.
     */
    private static void watchService() {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            CONFIG_FOLDER.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                try {
                    WatchKey key = watcher.take(); // Blocks until an event
                    /* Since there are two times when the listener is notified when a file is saved,
                     * once for the actual content and another time for the timestamp updated,
                     * sleeping will capture both into the same poll event list.
                     * From there, we can filter out what we have already seen to avoid updating more than once. */
                    Thread.sleep(100);
                    List<String> alreadySeen = new ArrayList<>();
                    for (WatchEvent<?> pollEvent : key.pollEvents()) {
                        if (pollEvent.kind() == StandardWatchEventKinds.OVERFLOW) continue;
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> event = (WatchEvent<Path>) pollEvent;
                        Path context = event.context();
                        String configName = context.getFileName().toString().replace(".json", "");
                        Class<? extends AbstractConfig> configClass = sNameToClass.get(configName);
                        if (configClass != null) {
                            if (alreadySeen.contains(configName)) continue;
                            System.out.printf("Config hot %s reloaded%n", configName);
                            AbstractConfig config = Configs.read(configClass);
                            sConfigMap.put(configClass, config);
                            notifyUpdated(configClass);
                            alreadySeen.add(configName);
                        } else {
                            System.err.printf("Unknown file %s%n", context);
                        }
                    }
                    if (!key.reset()) {
                        break;
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

    private static <T extends AbstractConfig> T read(Class<T> configClass) {
        Path configFile = getFileForConfig(configClass);
        String configClassName = configClass.getSimpleName();
        if (!Files.exists(configFile)) {
            String errorMessage = String.format("A config file was not found for %s", configClassName);
            throw new RuntimeException(errorMessage);
        }
        try {
            return sMapper.readValue(configFile.toFile(), configClass);
        } catch (IOException readException) {
            readException.printStackTrace();
            String errorMessage = String.format("An error occurred trying to read config for class %s%n", configClassName);
            try {
                System.out.printf("%s. See here for a default JSON file and double-check yours:%n%s%n",
                        errorMessage,
                        sMapper.defaultPrettyPrintingWriter().writeValueAsString(configClass.getConstructor().newInstance())
                );
            } catch (IOException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
            throw new RuntimeException(errorMessage, readException);
        }
    }

    private static Path getFileForConfig(Class<? extends AbstractConfig> configClass) {
        return CONFIG_FOLDER.resolve(String.format("%s.json", configClass.getSimpleName()));
    }

    private static <T extends AbstractConfig> void writeConfig(T newConfig) throws IOException {
        Path file = getFileForConfig(newConfig.getClass());
        Files.createDirectories(file.getParent().getParent());
        sMapper.defaultPrettyPrintingWriter().writeValue(file.toFile(), newConfig);
    }
}
