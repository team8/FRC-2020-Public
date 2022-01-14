package com.palyrobotics.frc2020.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
//import org.photonvision.PhotonCamera;

import java.util.Arrays;
import java.util.Iterator;

public class PiCommunicator {

	private static NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private NetworkTable mPhotonTable;
	private NetworkTable mCameraTable;
	private static double[] mZeroArray = new double[]{0, 0, 0, 0, 0, 0, 0, 0};
	private static final double kMetersToInches = 39.3701;
	boolean temp = false;

	public PiCommunicator (){
		mPhotonTable = sNetworkTableInstance.getTable("photonvision");
		//camera name is bozo. this is important you cannot change it :D
		mCameraTable = sNetworkTableInstance.getTable("photonvision").getSubTable("bozo");
	}

	private static PiCommunicator sInstance = new PiCommunicator();
	public static PiCommunicator getInstance() {
		return sInstance;
	}

	public double[] getPositionTemp(){
		if(hasTarget()){
			return mCameraTable.getEntry("targetPose").getDoubleArray(mZeroArray);
		}
		return mZeroArray;
	}

	/* takes position returned by Photon Vision and converts it to world pos
	diagram :D - https://drive.google.com/file/d/1Z6OApm3B9cS5oUGLi73PjfMO6X-2zCLW/view?usp=sharing
	 */
	public Pose2d getRobotPositionFromTarget(){
		double[] position = getPositionTemp();
		//for debugging
		//System.out.println("x: " + x + ", y: " + y + "angle: " + position[2]);
		if(!Arrays.equals(position, mZeroArray)){
			//conversion
			double x = position[0] * kMetersToInches;
			double y = position[1] * kMetersToInches;
			/*necessary because the xDist given by solvePnP in PhotonVision
			is relative to the plane between the target and the camera,
			and is therefore kind of like a hypotenuse, needs to be "rotated"
			such that it is on the flat ground plane.*/
			double xDist = getDistanceFromTargetPitch();
			//in degrees
			double angle = position[2];
			double hypot = Math.sqrt(Math.pow(xDist,2) + Math.pow(y, 2));
			return new Pose2d(hypot * Math.cos(Math.toRadians(angle)), hypot * Math.sin(Math.toRadians(angle)), new Rotation2d());
		}else{
			return new Pose2d(0,0, new Rotation2d());
		}
	}

	public double getPitchToTarget(){
		return mCameraTable.getEntry("targetPitch").getDouble(0.0);
	}

	public double getDistanceFromTargetPitch() {
		double cameraPitch = 35.15378286; // NEED TO TUNE DOES NOT WORK LOL
		double a2 = getPitchToTarget();
		double h1 = 33.50; //NEED TO MEASURE AS WELL
		double h2 = 90.5;
		return Math.max(0.0, ((h2 - h1) / Math.tan(Math.toRadians(cameraPitch + a2))));
	}

	public double getDistanceFromTargetHypot(double x){
		double h1 = 33.50;
		double h2 = 90.5;
		/*math.abs is just a failsafe, should not be needed,
		as the double x should be the hypotenuse of the triangle in this case,
		and therefore not shorter than h2-h1, as that would be the y portion of the triangle */
		return Math.sqrt(Math.abs(Math.pow(x, 2) - Math.pow(h2 - h1, 2)));
	}

	//for debugging
	public void printKeys() {
		Iterator<String> keyIterator = mPhotonTable.getKeys().iterator();
		String str = "";
		while (keyIterator.hasNext()) {
			str += keyIterator.next() + ", ";
		}
		System.out.println(str);
	}

	//if it detects a target
	public boolean hasTarget(){
		return mCameraTable.getEntry("hasTarget").getBoolean(false);
	}
}
