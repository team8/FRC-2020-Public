package com.palyrobotics.frc2018.subsystems.controllers;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.util.DriveSignal;
import com.palyrobotics.frc2018.util.Pose;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

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
		this.mPower = Constants.kTurnInPlacePower;
		this.mCachedPose = currentPose;
		this.mTargetHeading = this.mCachedPose.heading + heading;
		Logger.getInstance().logSubsystemThread(Level.INFO, "Starting Heading", this.mCachedPose.heading);
		Logger.getInstance().logSubsystemThread(Level.INFO, "Target Heading", this.mTargetHeading);

	}

	@Override
	public DriveSignal update(RobotState state) {
		if(this.onTarget()) {
			return DriveSignal.getNeutralSignal();
		}
		mCachedPose = state.drivePose;
		//System.out.println("Current Pose: " + mCachedPose.heading);
		DriveSignal output = DriveSignal.getNeutralSignal();
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
		double tolerance = Constants.kAcceptableTurnAngleError;
		return Math.abs(mCachedPose.heading - mTargetHeading) < tolerance;
	}

}
