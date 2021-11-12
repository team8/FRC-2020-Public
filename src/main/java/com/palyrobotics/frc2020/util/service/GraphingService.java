package com.palyrobotics.frc2020.util.service;

import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.palyrobotics.frc2020.util.http.InputThread;
import com.palyrobotics.frc2020.util.http.LightHttpServer;
import edu.wpi.first.networktables.NetworkTable;
import org.json.JSONObject;

public class GraphingService implements RobotService {
    NetworkTable liveTable = LiveGraph.getTable();
    private JSONObject jsonData = new JSONObject();
    private InputThread mThread = new InputThread();

    @Override
    public void start() {
        mThread.start("chart");
        for (String s : liveTable.getKeys()) {
            jsonData.put(s, 0);
        }
        liveTable.addEntryListener(((table, key, entry, value, flags) -> {
            if (value.isBoolean()) {
                if (value.getBoolean()) {
                    jsonData.put(key, 1);
                } else {
                    jsonData.put(key, 0);
                }
                LiveGraph.setJSONData(jsonData);
            } else {
                jsonData.put(key, value.getDouble());
                LiveGraph.setJSONData(jsonData);
            }
            if (LightHttpServer.getConnected()) {
                mThread.run();
                LightHttpServer.getServer().run();
            }
        }), 0xFF);
    }
}
