package com.palyrobotics.frc2020.vision;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.photonvision.PhotonCamera;

import java.util.Iterator;

public class PiCommunicator {

	private static NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private NetworkTable sPhotonTable;
	private NetworkTable mCameraTable;
	private static double[] mZeroArray = new double[]{0, 0, 0, 0, 0, 0, 0, 0};
	boolean temp = false;

	public PiCommunicator (){
		sPhotonTable = sNetworkTableInstance.getTable("photonvision");
		mCameraTable = sNetworkTableInstance.getTable("photonvision").getSubTable("bozo");
	}

	private static PiCommunicator sInstance = new PiCommunicator();
	public static PiCommunicator getInstance() {
		return sInstance;
	}

	public double[] getPositionTemp(){
		if(hasTarget()){
			double[] targetPos = mCameraTable.getEntry("targetPose").getDoubleArray(mZeroArray);
			return targetPos;
		}
		return mZeroArray;
	}

	public void printKeys() {
		Iterator<String> keyIterator = sPhotonTable.getKeys().iterator();
		String str = "";
		while (keyIterator.hasNext()) {
			str += keyIterator.next() + ", ";
		}
		System.out.println(str);
	}

	public boolean hasTarget(){
		return mCameraTable.getEntry("hasTarget").getBoolean(false);
	}
}
