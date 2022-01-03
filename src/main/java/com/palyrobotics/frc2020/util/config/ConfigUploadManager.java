package com.palyrobotics.frc2020.util.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.http.HttpInput;

import org.json.JSONObject;

public class ConfigUploadManager {

	private JSONObject config = new JSONObject();
	private static ConfigUploadManager mUploadManager = new ConfigUploadManager();

	private ConfigUploadManager() {
	}

	public static ConfigUploadManager getInstance() {
		return mUploadManager;
	}

	public void updateConfig(JSONObject newConfig) {
		config = newConfig;
		try {
			update();
		} catch (Error | IllegalAccessException | NoSuchFieldException e) {
			Log.error(e.toString());
		}
	}

	public boolean update() throws IllegalAccessException, NoSuchFieldException {
		if (!config.isEmpty()) {
			Iterator<String> keys = new JSONObject(config.get("config")).keys();

			while (keys.hasNext()) {
				String currentKey = keys.next();
				System.out.println(currentKey);
				JSONObject currentConfig = new JSONObject(config.get(currentKey));
				Class<? extends ConfigBase> configClass = Configs.getClassFromName(currentKey);
				ConfigBase configObject = Configs.get(configClass);
				Iterator<String> configKeys = currentConfig.keys();
				while (configKeys.hasNext()) {
					Field field = getField(configClass, configKeys.next());
					Configs.set(configObject, configClass, field, new JSONObject(configClass.toString()));
				}
				Configs.save(configClass);
			}

			Iterator<String> configIterator = Configs.getActiveConfigNames().iterator();
			JSONObject configJson = new JSONObject();
			Object temp;
			while (configIterator.hasNext()) {
				temp = configIterator.next();
				configJson.put(temp.toString(), new JSONObject(Configs.get(Configs.getClassFromName(temp.toString())).toString()));
			}
			HttpInput.getInstance().setConfigInput(configJson);

			return true;
		} else {
			System.out.println("idk");
			return false;
		}
	}

	private Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
		var fields = new HashMap<String, Field>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			fields.putAll(Arrays.stream(c.getDeclaredFields())
					.collect(Collectors.toMap(Field::getName, Function.identity())));
		}
		return Optional.ofNullable(fields.get(name)).orElseThrow(NoSuchFieldException::new);
	}
}
