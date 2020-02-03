package com.palyrobotics.frc2020.util.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LiveGraph {

	private static NetworkTable sLiveTable = NetworkTableInstance.getDefault().getTable("control-center-live");

	private LiveGraph() {
	}

	public static void add(String key, double value) {
		sLiveTable.getEntry(key).setDouble(value);
	}
}
