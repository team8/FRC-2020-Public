package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkSignal;

/**
 * Turns drivetrain using the gyroscope and bang-bang control loop
 * 
 * @author Robbie, Nihar
 *
 */
public class BangBangTurnAngleController implements Drive.DriveController {

	private double mPower;
	private double mTargetHeading;
	private Pose mCachedPose;

	/**
	 * @param currentPose
	 *            Pass in the latest robot state
	 * @param heading
	 *            Degrees relative to current state to turn
	 */
	public BangBangTurnAngleController(Pose currentPose, double heading) {
		this.mPower = -DrivetrainConstants.kTurnInPlacePower;
		this.mCachedPose = currentPose;
		this.mTargetHeading = this.mCachedPose.heading + heading;
//		Logger.getInstance().logSubsystemThread(Level.INFO, "Starting Heading", this.mCachedPose.heading);
//		Logger.getInstance().logSubsystemThread(Level.INFO, "Target Heading", this.mTargetHeading);

	}

	@Override
	public SparkSignal update(RobotState state) {
		if(this.onTarget()) {
			return SparkSignal.getNeutralSignal();
		}
		mCachedPose = state.drivePose;
		SparkSignal output = SparkSignal.getNeutralSignal();
		if (Math.abs(mCachedPose.heading - mTargetHeading) < 35) {
			this.mPower *= .20;
		}
		if(mCachedPose.heading < mTargetHeading) {
			output.leftMotor.setPercentOutput(this.mPower);
			output.rightMotor.setPercentOutput(-(this.mPower));
		} else {
			output.leftMotor.setPercentOutput(-(this.mPower));
			output.rightMotor.setPercentOutput(this.mPower);
		}
		return output;
	}

	@Override
	public Pose getSetpoint() {
		mCachedPose.heading = mTargetHeading;
		Pose setpoint = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
		return mCachedPose;
	}

	@Override
	public boolean onTarget() {
		double tolerance = DrivetrainConstants.kAcceptableTurnAngleError;
		System.out.println(mCachedPose.heading - mTargetHeading);
		return Math.abs(mCachedPose.heading - mTargetHeading) < tolerance;
	}

}
