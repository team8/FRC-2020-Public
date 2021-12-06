package com.palyrobotics.frc2020.vision;

import java.util.Iterator;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class PiCommunicator {

	private static NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private NetworkTable sPhotonTable;
	private NetworkTable mCameraTable;
	private static double[] mZeroArray = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
	boolean temp = false;

	public PiCommunicator() {
		sPhotonTable = sNetworkTableInstance.getTable("photonvision");
		mCameraTable = sNetworkTableInstance.getTable("photonvision").getSubTable("bozo");
	}

	private static PiCommunicator sInstance = new PiCommunicator();

	public static PiCommunicator getInstance() {
		return sInstance;
	}

	public double[] getPositionTemp() {
		if (hasTarget()) {
			double[] targetPos = mCameraTable.getEntry("targetPose").getDoubleArray(mZeroArray);
			return targetPos;
		}
		return mZeroArray;
	}

	public Pose2d getRobotPositionFromTarget() {
		double[] position = getPositionTemp();
		System.out.println("x: " + position[0] + ", y: " + position[1] + "angle: " + position[2]);
		if (!position.equals(mZeroArray)) {
			double xDist = getDistanceFromTarget(position[0]);
			double yDist = position[1];
			double angle = position[2];
			double hypot = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
			return new Pose2d(hypot * Math.cos(Math.toRadians(angle)), hypot * Math.sin(Math.toRadians(angle)), new Rotation2d());
		} else {
			return new Pose2d(0, 0, new Rotation2d());
		}
	}

	public double getDistanceFromTarget(double x) {
		double h1 = 33.50;
		double h2 = 90.5;
		return Math.sqrt(Math.pow(x, 2) - Math.pow(h2 - h1, 2));
	}

	public void printKeys() {
		Iterator<String> keyIterator = sPhotonTable.getKeys().iterator();
		String str = "";
		while (keyIterator.hasNext()) {
			str += keyIterator.next() + ", ";
		}
		System.out.println(str);
	}

	public boolean hasTarget() {
		return mCameraTable.getEntry("hasTarget").getBoolean(false);
	}
}
