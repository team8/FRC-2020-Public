package com.palyrobotics.frc2018.vision.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VisionUtil {
	/**
	 * Computes parsing of streamed data (for now just prints to console)
	 * 
	 * @param raw_data
	 *            Raw JSON formatted data (String)
	 */
	public static JSONObject parseJSON(String raw_data) {
		if(raw_data == null || raw_data.equals("") || raw_data.equals("error: no devices/emulators found")) {
			return null;
		}

		//Create JSONObject from the raw String data
		JSONObject json = null;

		try {
			JSONParser parser = new JSONParser();
			json = (JSONObject) parser.parse(raw_data);
		} catch(ParseException e) {
			//This is spammy
			//Logger.getInstance().logRobotThread(Level.FINEST, e);
		}

		return json;
	}
}
