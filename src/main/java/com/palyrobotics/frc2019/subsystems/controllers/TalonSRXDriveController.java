package com.palyrobotics.frc2019.subsystems.controllers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.TalonSRXOutput;

import java.util.Optional;

/**
 * Created by Nihar on 2/12/17.
 * 
 * @author Nihar Controller used for running an offboard can talon srx control loop
 */
public class TalonSRXDriveController implements Drive.DriveController {
	private final DriveSignal mSignal;

	private RobotState mCachedState = null;

	private String canTableString;

	/**
	 * Constructs a drive controller to store a signal <br />
	 * 
	 * @param signal
	 */
	public TalonSRXDriveController(DriveSignal signal) {
		//Use copy constructors and prevent the signal passed in from being modified externally
		this.mSignal = new DriveSignal(new TalonSRXOutput(signal.leftMotor), new TalonSRXOutput(signal.rightMotor));
	}

	@Override
	public DriveSignal update(RobotState state) {
		mCachedState = state;

		Pose drivePose = mCachedState.drivePose;

		DashboardManager.getInstance().updateCANTable("dt_left_dist",
				Integer.toString((drivePose.leftMotionMagicPos.isPresent()) ? drivePose.leftMotionMagicPos.get() : 0));
		DashboardManager.getInstance().updateCANTable("dt_velocity_left",
				Integer.toString((drivePose.leftMotionMagicVel.isPresent()) ? drivePose.leftMotionMagicVel.get() : 0));

		DashboardManager.getInstance().updateCANTable("dt_right_dist",
				Integer.toString((drivePose.rightMotionMagicPos.isPresent()) ? drivePose.rightMotionMagicPos.get() : 0));
		DashboardManager.getInstance().updateCANTable("dt_velocity_right",
				Integer.toString((drivePose.rightMotionMagicVel.isPresent()) ? drivePose.rightMotionMagicVel.get() : 0));

		DashboardManager.getInstance().updateCANTable("dt_error_left", Double.toString(mSignal.leftMotor.getSetpoint() - drivePose.leftEnc));
		DashboardManager.getInstance().updateCANTable("dt_error_right", Double.toString(mSignal.rightMotor.getSetpoint() - drivePose.rightEnc));
        System.out.println("Talon SRX Drive Controller Left Setpoint: " + mSignal.leftMotor.getSetpoint());
        System.out.println("Talon SRX Drive Controller Right Setpoint: " + mSignal.rightMotor.getSetpoint());
		return this.mSignal;
	}

	@Override
	public Pose getSetpoint() {
		Pose output = mCachedState.drivePose.copy();
		switch(mSignal.leftMotor.getControlMode()) {
			case MotionMagic:
				output.leftEnc = mSignal.leftMotor.getSetpoint();
				output.leftEncVelocity = 0;
				break;
			case Position:
				output.leftEnc = mSignal.leftMotor.getSetpoint();
				output.leftEncVelocity = 0;
				break;
			case Velocity:
				output.leftEncVelocity = mSignal.leftMotor.getSetpoint();
				break;
			case PercentOutput:
				//Open loop motor
				break;
			case Follower:
				//Not Applicable
				break;
			case Current:
				//Open loop motor
				break;
			case MotionProfile:
				output.leftMotionMagicPos = Optional.of((int) mSignal.leftMotor.getSetpoint());
				output.leftMotionMagicVel = Optional.of(0);
				break;
			case MotionProfileArc:
				output.leftMotionMagicPos = Optional.of((int) mSignal.leftMotor.getSetpoint());
				output.leftMotionMagicVel = Optional.of(0);
				break;
			case Disabled:
				output = new Pose();
				break;
		}
		switch(mSignal.rightMotor.getControlMode()) {
			case MotionMagic:
				output.rightEnc = mSignal.rightMotor.getSetpoint();
				output.rightEncVelocity = 0;
				break;
			case Position:
				output.rightEnc = mSignal.rightMotor.getSetpoint();
				output.rightEncVelocity = 0;
				break;
			case Velocity:
				output.rightEncVelocity = mSignal.rightMotor.getSetpoint();
				break;
			case PercentOutput:
				//Open loop motor
				break;
			case Follower:
				//Not Applicable
				break;
			case Current:
				//Open loop motor
				break;
			case MotionProfile:
				output.rightMotionMagicPos = Optional.of((int) mSignal.rightMotor.getSetpoint());
				output.rightMotionMagicVel = Optional.of(0);
				break;
			case MotionProfileArc:
				output.rightMotionMagicPos = Optional.of((int) mSignal.rightMotor.getSetpoint());
				output.rightMotionMagicVel = Optional.of(0);
				break;
			case Disabled:
				output = new Pose();
				break;
		}
		return output;
	}

	@Override
	public boolean onTarget() {
		if(mCachedState == null) {
			return false;
		}
		double positionTolerance = (mSignal.leftMotor.gains.equals(Gains.vidarShortDriveMotionMagicGains)) ? Constants.kAcceptableShortDrivePositionError
				: Constants.kAcceptableDrivePositionError;
		double velocityTolerance = (mSignal.leftMotor.gains.equals(Gains.vidarShortDriveMotionMagicGains)) ? Constants.kAcceptableShortDriveVelocityError
				: Constants.kAcceptableDriveVelocityError;

		//Motion magic is not PID so ignore whether talon closed loop error is around
		if(mSignal.leftMotor.getControlMode().equals(ControlMode.MotionMagic)) {
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

	private void setCanTableString(double[] a) {
		canTableString = "";
		for(int i = 0; i < a.length - 1; i++) {
			canTableString = canTableString + Double.toString(a[i]) + ", ";
		}
		canTableString = canTableString + Double.toString(a[a.length - 1]);
	}

	public String getCanTableString() {
		return this.canTableString;
	}
}