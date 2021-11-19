package com.palyrobotics.frc2020.vision;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class PiCommunicator {

	private static PiCommunicator sInstance = new PiCommunicator();

	public static PiCommunicator getInstance() {
		return sInstance;
	}

	private static NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private static NetworkTable sPhotonTable = sNetworkTableInstance.getTable("photonvision");
	private static double[] mZeroArray = new double[]{0, 0, 0, 0, 0, 0, 0, 0};

	public static double[] getPositionTemp(){
		if(hasTarget()){
			double[] targetPos = sPhotonTable.getEntry("targetPose").getDoubleArray(mZeroArray);
			return targetPos;
		}
		return mZeroArray;
	}

	public static boolean hasTarget(){
		return sPhotonTable.getEntry("hasTarget").getBoolean(false);
	}
}
