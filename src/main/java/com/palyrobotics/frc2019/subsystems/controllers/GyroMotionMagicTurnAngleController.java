package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;

public class GyroMotionMagicTurnAngleController implements DriveController {
	private Pose mCachedPose;
	private final double mTargetHeading; //Absolute setpoint in degrees
	private double mLeftTarget, mRightTarget;
	private TalonSRXOutput mLeftOutput, mRightOutput;

	private final Gains mGains;
	private final int mCruiseVel, mMaxAccel;

	private final double kInchesPerDegree, kTicksPerInch;
	private final double kTolerance;

	/**
	 * 
	 * @param priorSetpoint
	 * @param angle
	 *            Relative setpoint in degrees
	 */
	public GyroMotionMagicTurnAngleController(Pose priorSetpoint, double angle) {
		mCachedPose = priorSetpoint;
		mTargetHeading = priorSetpoint.heading + angle;

		mGains = Gains.vidarTurnMotionMagicGains;
		mCruiseVel = (int) Gains.kVidarTurnMotionMagicCruiseVelocity;
		mMaxAccel = (int) Gains.kVidarTurnMotionMagicMaxAcceleration;
		kInchesPerDegree = Constants.kDriveInchesPerDegree;
		kTicksPerInch = Constants.kDriveTicksPerInch;
		kTolerance = Constants.kAcceptableTurnAngleError;

		Logger.getInstance().logSubsystemThread(Level.FINE, "Current heading", mCachedPose.heading);
		Logger.getInstance().logSubsystemThread(Level.FINE, "Target heading", mTargetHeading);
		mLeftTarget = priorSetpoint.leftEnc - (angle * kInchesPerDegree * kTicksPerInch);
		mRightTarget = priorSetpoint.rightEnc + (angle * kInchesPerDegree * kTicksPerInch);

		mLeftOutput = new TalonSRXOutput();
		mLeftOutput.setMotionMagic(mLeftTarget, mGains, mCruiseVel, mMaxAccel);
		mRightOutput = new TalonSRXOutput();
		mRightOutput.setMotionMagic(mRightTarget, mGains, mCruiseVel, mMaxAccel);
	}

	@Override
	public DriveSignal update(RobotState state) {
		mCachedPose = state.drivePose;
		double error = mTargetHeading - mCachedPose.heading;
		//Compensate for current motion
		//error -= mCachedPose.headingVelocity*Constants.kSubsystemLooperDt;
		mLeftTarget = mCachedPose.leftEnc - (error * kInchesPerDegree * kTicksPerInch);
		mRightTarget = mCachedPose.rightEnc + (error * kInchesPerDegree * kTicksPerInch);
		mLeftOutput.setMotionMagic(mLeftTarget, mGains, mCruiseVel, mMaxAccel);
		mRightOutput.setMotionMagic(mRightTarget, mGains, mCruiseVel, mMaxAccel);
		return new DriveSignal(mLeftOutput, mRightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(0, 0, 0, 0, 0, 0, mTargetHeading, 0);
	}

	@Override
	public boolean onTarget() {
		//Wait for controller to be added before finishing routine
		//if (mLeftOutput.getSetpoint() != Robot.getRobotState().leftSetpoint) {
		//System.out.println("Mismatched desired talon and actual talon setpoints! desired, actual");
		//System.out.println("Left: " + mLeftOutput.getSetpoint()+", "+Robot.getRobotState().leftSetpoint);
		//return false;
		//}
		//else if (mRightOutput.getSetpoint() != Robot.getRobotState().rightSetpoint) {
		//System.out.println("Mismatched desired talon and actual talon setpoints! desired, actual");
		//System.out.println("Right: " + mRightOutput.getSetpoint()+", "+Robot.getRobotState().rightSetpoint);
		//return false;
		//}
		//else if (mLeftOutput.getControlMode() != Robot.getRobotState().leftControlMode) {
		//System.out.println("Mismatched desired talon and actual talon states!");
		//System.out.println(mLeftOutput.getControlMode() + ", "+Robot.getRobotState().leftControlMode);
		//return false;
		//}
		//else if (mRightOutput.getControlMode() != Robot.getRobotState().rightControlMode) {
		//System.out.println("Mismatched desired talon and actual talon states!");
		//System.out.println(mRightOutput.getControlMode()+","+Robot.getRobotState().rightControlMode);
		//return false;
		//}
		if(mCachedPose == null) {
			Logger.getInstance().logSubsystemThread(Level.FINER, "Cached pose is null");
			return false;
		}
		Logger.getInstance().logSubsystemThread(Level.FINEST, "On target", (Math.abs(Robot.getRobotState().drivePose.heading - mTargetHeading) < 3.4));
		Logger.getInstance().logSubsystemThread(Level.FINEST, Robot.getRobotState().drivePose.heading);
		return Math.abs(Robot.getRobotState().drivePose.heading - mTargetHeading) < kTolerance
				&& Math.abs(Robot.getRobotState().drivePose.headingVelocity) < 0.05;
	}

}
