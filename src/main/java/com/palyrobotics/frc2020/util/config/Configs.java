package com.palyrobotics.frc2020.util.config;

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

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.Filesystem;

/**
 * Configuration storage using JSON
 *
 * @author Quintin
 */
public class Configs {

	private static final String kConfigFolderName = "config", kLoggerTag = Util.classToJsonName(Configs.class);
	private static final Path kConfigFolder = Paths.get(Filesystem.getDeployDirectory().toString(), kConfigFolderName);
	private static final HashMap<String, Class<? extends ConfigBase>> sNameToClass = new HashMap<>();
	private static final HashMap<Class<? extends ConfigBase>, ConfigBase> sConfigMap = new HashMap<>(16);
	private static final HashMap<Class<? extends ConfigBase>, List<Runnable>> sListeners = new HashMap<>();
	private static ObjectMapper sMapper = new ObjectMapper();
	private static ObjectWriter sPrettyWriter = sMapper.writerWithDefaultPrettyPrinter();
	private static final Thread sModifiedListener = new Thread(Configs::watchService),
			sRobotThread = Thread.currentThread();

	static {
		// Allows us to serialize private fields
		sMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
	}

	static {
		sModifiedListener.start();
	}

	private Configs() {
	}

	public static ObjectMapper getMapper() {
		return sMapper;
	}

	public static <T extends ConfigBase> T get(Class<T> configClass, String name) {
		Path custom = resolveConfigPath(name);
		try {
			T value = sMapper.readValue(custom.toFile(), configClass);
			value.onPostUpdate();
			return value;
		} catch (IOException parseException) {
			throw handleParseError(parseException, configClass);
		}
	}

	private static Path resolveConfigPath(String name) {
		return kConfigFolder.resolve(String.format("%s.json", name));
	}

	private static RuntimeException handleParseError(IOException readException, Class<? extends ConfigBase> configClass) {
		String errorMessage = String.format(
				"An error occurred trying to read config for class %s%n%nSee here for default JSON: %s%n",
				configClass.getSimpleName(), getDefaultJson(configClass));
		return new RuntimeException(errorMessage, readException);
	}

	private static Optional<String> getDefaultJson(Class<? extends ConfigBase> configClass) {
		try {
			return Optional.ofNullable(String.format("See here for a default JSON file:%n%s%n",
					sPrettyWriter.writeValueAsString(configClass.getConstructor().newInstance())));
		} catch (IOException | NoSuchMethodException | IllegalAccessException | InstantiationException |
				InvocationTargetException exception) {
			Log.error(kLoggerTag,
					"Could not show default JSON representation. Something is wrong with the config class definition.",
					exception);
			return Optional.empty();
		}
	}

	/**
	 * Listen for changes in a configuration file. An on-changed event is fired once when this function
	 * is invoked for initial setup.
	 *
	 * @param configClass Class of the config.
	 * @param onChanged   Listener which consumes new config instance.
	 * @param <T>         Type of the config class. This is usually inferred from the class argument.
	 */
	public static <T extends ConfigBase> void listen(Class<T> configClass, Consumer<T> onChanged) {
		onChanged.accept(get(configClass));
		var consumers = sListeners.computeIfAbsent(configClass, newValue -> new ArrayList<>(1));
		consumers.add(() -> onChanged.accept(get(configClass))); // TODO kinda whack
	}

	/**
	 * Retrieve the singleton for this given config class.
	 *
	 * @param  configClass Class of the config.
	 * @param  <T>         Type of the config class. This is usually inferred from the class argument.
	 * @return             Singleton or null if not found / registered.
	 */
	@SuppressWarnings ("unchecked")
	public static <T extends ConfigBase> T get(Class<T> configClass) {
		T config = (T) sConfigMap.get(configClass);
		if (config == null) {
			config = read(configClass);
			sConfigMap.put(configClass, config);
			sNameToClass.put(configClass.getSimpleName(), configClass);
		}
		return config;
	}

	/**
	 * Read the given config from the filesystem. There must be a file and it must be valid mappable
	 * JSON, desired behavior is to crash if else. In attempt to help the user when there is an invalid
	 * JSON file, a default empty class of the same type is printed to console to show desired format
	 * (helpful for debugging).
	 *
	 * @param  configClass      Class of the config.
	 * @param  <T>              Type of the config. Must extend {@link ConfigBase}.
	 * @return                  Instance of given type.
	 * @throws RuntimeException when the file cannot be found or it could not be parsed. This is
	 *                          considered a critical error.
	 */
	private static <T extends ConfigBase> T read(Class<T> configClass) {
		Path configFile = getFileForConfig(configClass);
		String configClassName = configClass.getSimpleName();
		if (!Files.exists(configFile)) {
			String errorMessage = String.format("A config file was not found for %s. Critical error, aborting.%n%n%s%n",
					configClassName, getDefaultJson(configClass));
			throw new RuntimeException(errorMessage);
		}
		try {
			T value = sMapper.readValue(configFile.toFile(), configClass);
			value.onPostUpdate();
			return value;
		} catch (IOException readException) {
			RuntimeException exception = handleParseError(readException, configClass);
			Log.error(kLoggerTag, String.format("Error reading config %s", configClassName), readException);
			throw exception;
		}
	}

	private static Path getFileForConfig(Class<? extends ConfigBase> configClass) {
		return resolveConfigPath(configClass.getSimpleName());
	}

	public static <T extends ConfigBase> boolean save(Class<T> configClass) {
		try {
			saveOrThrow(configClass);
			return true;
		} catch (IOException saveException) {
			Log.error(kLoggerTag, String.format("Could not save config %s", configClass.getSimpleName()),
					saveException);
			return false;
		}
	}

	public static void saveOrThrow(Class<? extends ConfigBase> configClass) throws IOException {
		writeConfig(sConfigMap.get(configClass));
	}

	private static <T extends ConfigBase> void writeConfig(T newConfig) throws IOException {
		Path file = getFileForConfig(newConfig.getClass());
		// Creates all folders leading up to target files's parent folder
		Files.createDirectories(file.getParent().getParent());
		sPrettyWriter.writeValue(file.toFile(), newConfig);
	}

	public static Class<? extends ConfigBase> getClassFromName(String name) {
		return sNameToClass.get(name);
	}

	public static void set(ConfigBase config, Object object, Field field, Object value) throws IllegalAccessException {
		field.set(object, value);
		notifyUpdated(config.getClass());
	}

	private static void notifyUpdated(Class<? extends ConfigBase> configClass) {
		// TODO nasty
		Optional.ofNullable(sListeners.get(configClass)).ifPresent(listeners -> listeners.forEach(Runnable::run));
	}

	/**
	 * If different, replace the working specified config with the one on the filesystem. This also
	 * updates listeners.
	 *
	 * @param  configClass Class of the config.
	 * @return             Whether or not the config was updated.
	 */
	public static boolean reload(Class<? extends ConfigBase> configClass) {
		ConfigBase existing = sConfigMap.get(configClass), onFile = read(configClass);
		try {
			if (existing == null || !sMapper.writeValueAsString(existing).equals(sMapper.writeValueAsString(onFile))) {
				sConfigMap.put(configClass, read(configClass));
				notifyUpdated(configClass);
				return true;
			}
		} catch (IOException exception) {
			Log.error(kLoggerTag, String.format("Could not reload configs %s", configClass.getSimpleName()), exception);
		}
		return false;
	}

	/**
	 * Helper method to use the {@link #sMapper} of this class to easily produce a JSON string. This
	 * handles errors internally.
	 *
	 * @param  object Any arbitrary object to try and write to JSON.
	 * @return        The object in JSON format if possible or else the default {@link #toString()}.
	 */
	public static String toJson(Object object) {
		try {
			return sPrettyWriter.writeValueAsString(object);
		} catch (IOException formatException) {
			Log.warn(kLoggerTag, String.format("Could not format %s as JSON", object.getClass().getSimpleName()),
					formatException);
			return object.toString();
		}
	}

	public static List<String> getActiveConfigNames() {
		return sConfigMap.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList());
	}

	/**
	 * Copy an object by converting it to its JSON representation then reads it into a new object. This
	 * is slow and creates garbage, so it should not be used often.
	 */
	@SuppressWarnings ("unchecked")
	public static <T> T copy(T toCopy) {
		try {
			return (T) sMapper.readValue(sMapper.writeValueAsString(toCopy), toCopy.getClass());
		} catch (IOException exception) {
			throw new IllegalArgumentException("Type supplied cannot be represented as JSON! Can not copy.", exception);
		}
	}

	/**
	 * This should be started in a new thread to watch changes for the folder containing the JSON
	 * configuration files. It detects when files are modified and written in the filesystem, then
	 * reloads them calling {@link #notifyUpdated(Class)}.
	 */
	private static void watchService() {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			kConfigFolder.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			while (!Thread.interrupted()) {
				try {
					WatchKey key = watcher.take(); // Blocks until an event
					/*
					* Since there are two times when the listener is notified when a file is saved,
					* once for the actual content and another time for the timestamp updated,
					* sleeping will capture both into the same poll event list. From there, we can
					* filter out what we have already seen to avoid updating more than once.
					*/
					Thread.sleep(100L);
					List<String> alreadySeen = new ArrayList<>();
					/*
					* We are on a different thread, so we must be careful updating variables on the
					* main thread. Current model is to force the robot thread to wait for a notify
					* once we are done updating variables from our thread.
					*/
					synchronized (sRobotThread) {
						sRobotThread.wait(100L); // In case something goes horribly wrong we can resume robot thread
						// execution
						for (WatchEvent<?> pollEvent : key.pollEvents()) {
							if (pollEvent.kind() == StandardWatchEventKinds.OVERFLOW) continue;
							@SuppressWarnings ("unchecked")
							WatchEvent<Path> event = (WatchEvent<Path>) pollEvent;
							Path context = event.context();
							String configName = context.getFileName().toString().replace(".json", "");
							Class<? extends ConfigBase> configClass = sNameToClass.get(configName);
							if (configClass != null) {
								if (alreadySeen.contains(configName)) continue;
								Log.info(kLoggerTag, String.format("Config named %s hot reloaded%n", configName));
								try {
									ConfigBase config = get(configClass);
									sMapper.readerForUpdating(config).readValue(getFileForConfig(configClass).toFile());
									config.onPostUpdate();
								} catch (IOException readException) {
									RuntimeException exception = handleParseError(readException, configClass);
									Log.error(kLoggerTag, String.format(
											"Error updating config for %s. Aborting reload.%n", configName), exception);
								}
								notifyUpdated(configClass);
								alreadySeen.add(configName);
							} else {
								Log.error(kLoggerTag, String.format("Unknown file %s%n", context));
							}
						}
						if (!key.reset()) {
							break;
						}
						sRobotThread.notifyAll();
					}
				} catch (InterruptedException exception) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (IOException exception) {
			Log.error(kLoggerTag, "Failed to watch filesystem for reloads", exception);
		}
	}
}
