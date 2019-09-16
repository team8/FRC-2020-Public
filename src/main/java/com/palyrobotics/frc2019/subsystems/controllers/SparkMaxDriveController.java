package com.palyrobotics.frc2019.subsystems.controllers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;

/**
 * Created by Robbie on 2/2/19.
 * 
 * @author Nihar Controller used for running an offboard can spark max
 */
public class SparkMaxDriveController implements Drive.DriveController {
	private final SparkSignal mSignal;

	private RobotState mCachedState = null;

	private String canTableString;

	/**
	 * Constructs a drive controller to store a signal <br />
	 * 
	 * @param signal
	 */
	public SparkMaxDriveController(SparkSignal signal) {
		//Use copy constructors and prevent the signal passed in from being modified externally
		this.mSignal = new SparkSignal(new SparkMaxOutput(signal.leftMotor), new SparkMaxOutput(signal.rightMotor));
	}

	@Override
	public SparkSignal update(RobotState state) {
		mCachedState = state;
		return this.mSignal;
	}

	@Override
	public Pose getSetpoint() {
		Pose output = mCachedState.drivePose.copy();
		switch(mSignal.leftMotor.getControlType()) {
			case kPosition:
				output.leftEnc = mSignal.leftMotor.getSetpoint();
				output.leftEncVelocity = 0;
				break;
			case kVelocity:
				output.leftEncVelocity = mSignal.leftMotor.getSetpoint();
				break;
			case kDutyCycle:
				//Open loop motor
				break;
		}

		switch(mSignal.rightMotor.getControlType()) {
			case kPosition:
				output.leftEnc = mSignal.leftMotor.getSetpoint();
				output.leftEncVelocity = 0;
				break;
			case kVelocity:
				output.leftEncVelocity = mSignal.leftMotor.getSetpoint();
				break;
			case kDutyCycle:
				//Open loop motor
				break;
		}


		return output;
	}

	@Override
	public boolean onTarget() {
		if(mCachedState == null) {
			return false;
		}
		double positionTolerance = (mSignal.leftMotor.getGains().equals(Gains.vidarShortDriveMotionMagicGains)) ? DrivetrainConstants.kAcceptableShortDrivePositionError
				: DrivetrainConstants.kAcceptableDrivePositionError;
		double velocityTolerance = (mSignal.leftMotor.getGains().equals(Gains.vidarShortDriveMotionMagicGains)) ? DrivetrainConstants.kAcceptableShortDriveVelocityError
				: DrivetrainConstants.kAcceptableDriveVelocityError;

		//Motion magic is not PID so ignore whether talon closed loop error is around
		if(mSignal.leftMotor.getControlType().equals(ControlMode.MotionMagic)) {
			return (Math.abs(mCachedState.drivePose.leftEnc - mSignal.leftMotor.getSetpoint()) < positionTolerance)
					&& (Math.abs(mCachedState.drivePose.leftEncVelocity) < velocityTolerance)
					&& (Math.abs(mCachedState.drivePose.rightEnc - mSignal.rightMotor.getSetpoint()) < positionTolerance)
					&& (Math.abs(mCachedState.drivePose.rightEncVelocity) < velocityTolerance);
		}
		if(!mCachedState.drivePose.leftError.isPresent() || !mCachedState.drivePose.rightError.isPresent()) {
			//System.err.println("Talon closed loop error not found!");
			return false;
		}
		return (Math.abs(mCachedState.drivePose.leftError.get()) < positionTolerance) && (Math.abs(mCachedState.drivePose.rightError.get()) < positionTolerance
				&& Math.abs(mCachedState.drivePose.leftEncVelocity) < velocityTolerance
				&& Math.abs(mCachedState.drivePose.rightEncVelocity) < velocityTolerance);
	}


}