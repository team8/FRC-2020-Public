package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.control.Gains;
import com.palyrobotics.frc2019.util.control.SynchronousPID;

public class DriveStraightController implements DriveController {

	private Pose mCachedPose;
	private double mTarget;
	private Gains mGains;

	private SynchronousPID forwardPID, headingPID;

	private final double kTolerance;
	private SparkDriveSignal mOutput = new SparkDriveSignal();

	public DriveStraightController(Pose priorSetPoint, double distance) {
		mTarget = (priorSetPoint.leftEncoderPosition + priorSetPoint.rightEncoderPosition) / 2 + distance;
//		Logger.getInstance().logSubsystemThread(Level.INFO, "Target", target);
		mCachedPose = priorSetPoint;
		mGains = new Gains(0.00035, 0.000004, 0.002, 0, 200);
		kTolerance = DrivetrainConstants.kAcceptableDrivePositionError;
		forwardPID = new SynchronousPID(mGains.p, mGains.i, mGains.d, mGains.iZone);
		headingPID = new SynchronousPID(Gains.kVidarDriveStraightTurnP, 0, 0.005);
		forwardPID.setOutputRange(-1, 1);
		headingPID.setOutputRange(-0.2, 0.2);
		forwardPID.setSetPoint(mTarget);
		headingPID.setSetPoint(priorSetPoint.heading);
	}

	@Override
	public boolean onTarget() {
		if(mCachedPose == null) {
//			Logger.getInstance().logSubsystemThread(Level.FINER, "Cached pose is null");
			return false;
		}

		return Math.abs(Robot.getRobotState().drivePose.heading) < kTolerance
				&& Math.abs((Robot.getRobotState().drivePose.leftEncoderPosition + Robot.getRobotState().drivePose.rightEncoderPosition) / 2 - mTarget) < kTolerance
				&& Math.abs(Robot.getRobotState().drivePose.leftEncoderVelocity) < 0.05 && Math.abs(Robot.getRobotState().drivePose.rightEncoderVelocity) < 0.05;
	}

	@Override
	public SparkDriveSignal update(RobotState state) {
		mCachedPose = state.drivePose;
		double distanceSoFar = state.drivePose.leftEncoderPosition + state.drivePose.rightEncoderPosition;
		distanceSoFar /= 2;
		double throttle = forwardPID.calculate(distanceSoFar);
		//double turn = headingPID.calculate(state.drivePose.heading) * Constants.kDriveInchesPerDegree;
		double turn = 0;
		mOutput.leftOutput.setPercentOutput(throttle + turn);
		mOutput.rightOutput.setPercentOutput(throttle - turn);
		return mOutput;
	}

	@Override
	public Pose getSetPoint() {
		Pose pose = new Pose();
		pose.rightEncoderPosition = mTarget;
		pose.leftEncoderPosition = mTarget;
		return pose;
	}

}
