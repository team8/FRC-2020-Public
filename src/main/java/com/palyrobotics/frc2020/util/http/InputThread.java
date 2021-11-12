package com.palyrobotics.frc2020.util.http;

import java.util.ArrayList;

import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.palyrobotics.frc2020.util.service.LogEntry;

import org.json.*;

public class InputThread implements Runnable {

	private Thread tThread;
	private JSONObject json = new JSONObject();
	private ArrayList<LogEntry> logs = new ArrayList<>();
	private JSONArray array = new JSONArray();
	private String dataType = null;

	private final int logSize = 50;

	@Override
	public void run() {
		//addLog(new LogEntry((long) time, 1, null, "test", null));
		array.clear();
		int startInd = 0;
		if (logs.size() > logSize) {
			startInd = logs.size() - logSize;
		}
		for (int i = startInd; i < logs.size() - startInd; i++) {
			array.put(logs.get(i).toString());
		}
		json.put("logs", array);
		json.put("graphData", LiveGraph.getJSONData());
		if (dataType.equals("logs")) {
			HttpInput.getInstance().setLogInput(json);
		} else if (dataType.equals("chart")) {
			HttpInput.getInstance().setChartInput(json);
		}
		//}

	}

	public void addLog(LogEntry log) {
		logs.add(log);
	}

	public void start(String sendType) {
		dataType = sendType;
		if (tThread == null) {
			tThread = new Thread(this, "inputThread");
			tThread.start();
		}
	}
}
