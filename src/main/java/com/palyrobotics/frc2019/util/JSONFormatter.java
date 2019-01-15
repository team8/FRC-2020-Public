package com.palyrobotics.frc2019.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class JSONFormatter {
	private static Map<String, Object> properties = new HashMap<String, Object>();
	private static JSONParser parser = new JSONParser();
	
	private static File defaultFile = new File("/home/lvuser/constants/fields/Team8Field.json");
	// TODO: make some actual default json file because stuff
	
	/**
	 * Loads the directory of a file that is deployed to a roboRIO
	 * @param fileName
	 * @return
	 */
	public static File loadFileDirectory(String folderName, String fileName) {
		// Works for roboRIO only I think, since it's not possible to deploy to computers or something I dunno
		
		String os = System.getProperty("os.name");
		String directory;
		if (os.startsWith("Mac") || os.startsWith("Windows")) {
			directory = "."+File.separatorChar+folderName+File.separatorChar;
		} else {
			// Pray that this is a roborio
			directory = "/home/lvuser/"+folderName+"/";
		}
		
		if (fileExists(new File(directory+fileName))) { // should work idk
			System.out.println("Loaded directory of " + fileName);
			return new File(directory + fileName);
		} else {
			if (Files.exists(defaultFile.toPath())) {
				System.out.println("File doesn't exist, defaulting to Team8Field.json");
				return defaultFile;
			} else { // shouldn't happen unless the default file isn't deployed, either
				System.out.println("File doesn't exist, no default file");
				return null;
			}
		}
	}
	
	/**
	 * In a given file, writes using the given hashmap
	 * @param fileName
	 * @param newValues
	 */
	public static void writeFile(File fileName, Map<String, String> newValues) { // probably won't be used
		JSONObject json = newJSON(fileName);
		Object[] keys = newValues.keySet().toArray();
		Object[] values = newValues.values().toArray();
		for (int i = 0; i < keys.length; i++) {
			json.put(keys[i], values[i]);
		}
		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(json.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a JSON file, and returns the value of a key in the file
	 * @param fileName
	 * @param key
	 * @return value
	 */
	public static Object getValueInFile(File fileName, String key) {
		return readFile(fileName).get(key);
	}
	
	/**
	 * Reads a JSON file, and returns it as a hash map
	 * @param fileName
	 * @return properties
	 */
	public static Map<String, Object> readFile(File fileName) {
		if (fileExists(fileName)) {
			setProperties(newJSON(fileName));
		} else {
			System.out.println(fileName.toString() + " not found, defaulting to " + defaultFile.toString());
			setProperties(newJSON(defaultFile));
		}
		return properties;
	}

	/**
	 * Creates a new JSONObject that contains values from the file
	 * @param fileName
	 * @return a new JSONObject
	 */
	public static JSONObject newJSON(File fileName) {
		try {
			JSONObject json = (JSONObject) parser.parse(new FileReader(fileName));
			return json;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) { // sketchy
			System.out.println(fileName.toString() + " cannot be read, deafulting to " + defaultFile.toString());
			try {
				JSONObject json = (JSONObject) parser.parse(new FileReader(fileName));
				return json;
			} catch (FileNotFoundException f) {
				e.printStackTrace();
			} catch (ParseException f) {
				e.printStackTrace();
			} catch (IOException f) {
				e.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets properties to a new json file
	 * @param json
	 */
	private static void setProperties(JSONObject json) {
		Set keys = json.keySet();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			properties.put(key, json.get(key));
		}
	}
	
	private static boolean fileExists(File fileName) { // should work maybe
		return Files.exists(fileName.toPath());
	}
}