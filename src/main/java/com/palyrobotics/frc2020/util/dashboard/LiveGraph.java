package com.palyrobotics.frc2020.util.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LiveGraph {

	private static final NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private static final NetworkTable sLiveTable = sNetworkTableInstance.getTable("control-center-live");

//	private LiveGraph() {
//		sNetworkTableInstance.setUpdateRate(0.01);
//	}

	public static void add(String key, double value) {
//		sLiveTable.getEntry(key).setDouble(value);
	}

	public static void add(String key, boolean value) {
		sLiveTable.getEntry(key).setBoolean(value);
	}
}
