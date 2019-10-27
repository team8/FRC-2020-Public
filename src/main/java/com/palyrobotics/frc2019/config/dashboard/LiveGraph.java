package com.palyrobotics.frc2019.config.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LiveGraph {

    private static LiveGraph sInstance = new LiveGraph();

    private static NetworkTable sLiveTable = NetworkTableInstance.getDefault().getTable("control-center-live");

    public static LiveGraph getInstance() {
        return sInstance;
    }

    public void add(String key, double value) {
        sLiveTable.getEntry(key).setDouble(value);
    }
}
