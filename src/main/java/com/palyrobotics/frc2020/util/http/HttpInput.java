package com.palyrobotics.frc2020.util.http;

import java.util.Iterator;

import org.json.JSONObject;

public class HttpInput {

	private String input = null;
	private JSONObject output = new JSONObject();
	private JSONObject logInput = new JSONObject();
	private JSONObject chartInput = new JSONObject();

	private HttpInput() {

	}

	public static HttpInput getInstance() {
		return sHttpInput;
	}

	private static HttpInput sHttpInput = new HttpInput();

	public void setInput(String newInput) {
		input = newInput;
	}

	public void setChartInput(JSONObject newInput) {
		chartInput = newInput;
	}

	public void setLogInput(JSONObject newInput) {
		logInput = newInput;
	}

	public JSONObject getInput() {

		if (!chartInput.isEmpty()) {

			Iterator<String> chartKeys = chartInput.keys();

			while (chartKeys.hasNext()) {
				String key = chartKeys.next();
				if (key == "graphData") {
					output.put(key, chartInput.get(key));
				}
			}
		}

		if (!logInput.isEmpty()) {

			Iterator<String> logKeys = logInput.keys();

			while (logKeys.hasNext()) {
				String key = logKeys.next();
				if (key == "logs") {
					output.put(key, logInput.get(key));
				}
			}
		}

		return output;
	}
}
