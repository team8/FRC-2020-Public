package com.palyrobotics.frc2020.config.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LiveGraph {

	private static LiveGraph sInstance = new LiveGraph();

	public static LiveGraph getInstance() {
		return sInstance;
	}

	private static NetworkTable sLiveTable = NetworkTableInstance.getDefault().getTable("control-center-live");

	public void add(String key, double value) {
		sLiveTable.getEntry(key).setDouble(value);
	}
}
