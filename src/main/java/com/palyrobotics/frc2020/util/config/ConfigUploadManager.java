package com.palyrobotics.frc2020.util.config;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.ProfiledGains;
import com.palyrobotics.frc2020.util.http.HttpInput;

import org.json.JSONObject;

public class ConfigUploadManager {

	private JSONObject config = new JSONObject();
	private static ConfigUploadManager mUploadManager = new ConfigUploadManager();

	private static ObjectMapper sMapper = Configs.getMapper();

	private ConfigUploadManager() {
	}

	public static ConfigUploadManager getInstance() {
		return mUploadManager;
	}

	public void updateConfig(JSONObject newConfig) {
		config = newConfig;
		try {
			update();
		} catch (Error | IllegalAccessException | NoSuchFieldException | JsonProcessingException e) {
			Log.error(e.toString());
		}
	}

	public boolean update() throws IllegalAccessException, NoSuchFieldException, JsonProcessingException {
		if (!config.isEmpty()) {
			Iterator<String> keys = config.keys();

			while (keys.hasNext()) {
				String currentKey = keys.next();
				JSONObject currentConfig = (JSONObject) config.get(currentKey);
				Class<? extends ConfigBase> configClass = Configs.getClassFromName(currentKey);
				ConfigBase configObject = Configs.get(configClass);
				Iterator<String> configKeys = currentConfig.keys();
				while (configKeys.hasNext()) {
					String temp = configKeys.next();
					Field field = getField(configClass, temp);
//					Object newFieldValue = sMapper.readValue((String) currentConfig.get(temp), field.getType());
					try {
						if (field.getType().getName() == "com.palyrobotics.frc2020.util.control.Gains") {
							JSONObject internalJson = (JSONObject) currentConfig.get(temp);
							Gains tGains = new Gains(internalJson.getBigDecimal("p").doubleValue(), internalJson.getBigDecimal("i").doubleValue(), internalJson.getBigDecimal("d").doubleValue(), internalJson.getBigDecimal("f").doubleValue(), internalJson.getBigDecimal("iZone").doubleValue(), internalJson.getBigDecimal("iMax").doubleValue());
							Configs.set(configObject, configObject, field, tGains);
						} else if (field.getType().getName() == "com.palyrobotics.frc2020.util.control.ProfiledGains") {
							JSONObject internalJson = (JSONObject) currentConfig.get(temp);
							ProfiledGains tGains = new ProfiledGains(internalJson.getBigDecimal("p").doubleValue(), internalJson.getBigDecimal("i").doubleValue(), internalJson.getBigDecimal("d").doubleValue(), internalJson.getBigDecimal("f").doubleValue(), internalJson.getBigDecimal("iZone").doubleValue(), internalJson.getBigDecimal("iMax").doubleValue(), internalJson.getBigDecimal("acceleration").doubleValue(), internalJson.getBigDecimal("velocity").doubleValue(), internalJson.getBigDecimal("allowableError").doubleValue(), internalJson.getBigDecimal("minimumOutputVelocity").doubleValue());
							Configs.set(configObject, configObject, field, tGains);
						} else if (field.getType().getName() == "java.util.List") {
							/*try {
								List<Integer> tJsonList = new ArrayList(Arrays.asList((JSONArray) currentConfig.get(temp)));
								Configs.set(configObject, configObject, field, tJsonList);
							} catch (Exception e) {
								System.out.println(e);
							}
							try {
								List<String> tJsonList = new ArrayList(Arrays.asList((JSONArray) currentConfig.get(temp)));
								Configs.set(configObject, configObject, field, tJsonList);
							} catch (Exception e) {
								System.out.println(e);
							}*/
						} else {
							Configs.set(configObject, configObject, field, field.getType().getName() == "double" ? ((BigDecimal) currentConfig.get(temp)).doubleValue() : currentConfig.get(temp));
						}
					} catch (Exception e) {
						System.out.println(e);
					}
					Configs.save(configClass);
				}
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
