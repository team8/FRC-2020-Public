package com.palyrobotics.frc2020.util.http;

import java.util.Iterator;

import org.json.JSONObject;

public class HttpInput {

	private String input = null;
	private JSONObject output = new JSONObject();
	private JSONObject logInput = new JSONObject();
	private JSONObject chartInput = new JSONObject();
	private JSONObject configInput = new JSONObject();
	private JSONObject telemetryInput = new JSONObject();

	private HttpInput() {
		output.put("telemetry", telemetryInput);
		output.put("config", configInput);
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

	public void setConfigInput(JSONObject newInput) {
		configInput = newInput;
	}

	public void setTelemetry(JSONObject newInput) {
		telemetryInput = newInput;
	}

	public JSONObject getInput() {

		if (!telemetryInput.isEmpty()) {

			output.put("telemetry", telemetryInput);

		}

		if (!configInput.isEmpty()) {

			output.put("config", configInput);

		}

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
