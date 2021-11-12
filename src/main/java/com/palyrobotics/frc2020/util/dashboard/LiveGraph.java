package com.palyrobotics.frc2020.util.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.json.JSONObject;

public class LiveGraph {

	private static final NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private static final NetworkTable sLiveTable = sNetworkTableInstance.getTable("control-center-live");
	private static JSONObject mData = new JSONObject();

//	private LiveGraph() {
//		sNetworkTableInstance.setUpdateRate(0.01);
//	}

	public static void add(String key, double value) {
		sLiveTable.getEntry(key).setDouble(value);
	}

	public static void add(String key, boolean value) {
		sLiveTable.getEntry(key).setBoolean(value);
	}

	public static NetworkTable getTable() {
		return sLiveTable;
	}

	public static void setJSONData(JSONObject data) {
		mData = data;
	}

	public static JSONObject getJSONData() {
		return mData;
	}
}
