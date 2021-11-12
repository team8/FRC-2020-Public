package com.palyrobotics.frc2020.util.http;

import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.palyrobotics.frc2020.util.service.LogEntry;
import org.json.*;

import java.util.ArrayList;

public class InputThread implements Runnable {
    private Thread tThread;
    private JSONObject json = new JSONObject();
    private ArrayList<LogEntry> logs = new ArrayList<>();
    private JSONArray array = new JSONArray();
    private String dataType = null;

    @Override
    public void run() {
        //addLog(new LogEntry((long) time, 1, null, "test", null));
        array.clear();
        for (int i = 0; i < logs.size(); i++) {
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