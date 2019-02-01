package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;

public class EncoderTurnAngleController implements DriveController {

	private Pose cachedPose;
	private double leftTarget;
	private double rightTarget;
	private double maxAccel;
	private double maxVel;
	private Gains mGains;
	private TalonSRXOutput leftOutput;
	private TalonSRXOutput rightOutput;

	public EncoderTurnAngleController(Pose priorSetpoint, double angle) {
		leftTarget = priorSetpoint.leftEnc + (angle * DrivetrainConstants.kDriveInchesPerDegree * DrivetrainConstants.kDriveTicksPerInch);
		rightTarget = priorSetpoint.rightEnc - (angle * DrivetrainConstants.kDriveInchesPerDegree * DrivetrainConstants.kDriveTicksPerInch);
		cachedPose = priorSetpoint;
		this.maxAccel = 72 * DrivetrainConstants.kDriveSpeedUnitConversion;
		this.maxVel = 36 * DrivetrainConstants.kDriveSpeedUnitConversion;

		if(OtherConstants.kRobotName.equals(OtherConstants.RobotName.VIDAR)) {
			mGains = new Gains(6.0, 0.01, 210, 2.0, 50, 0.0);
		}

		leftOutput = new TalonSRXOutput();
		leftOutput.setMotionMagic(leftTarget, mGains, (int) maxVel, (int) maxAccel);
		rightOutput = new TalonSRXOutput();
		rightOutput.setMotionMagic(rightTarget, mGains, (int) maxVel, (int) maxAccel);
	}

	@Override
	public boolean onTarget() {
		if(Robot.getRobotState().leftSetpoint != leftOutput.getSetpoint() || Robot.getRobotState().rightSetpoint != rightOutput.getSetpoint()
				|| Robot.getRobotState().leftControlMode != leftOutput.getControlMode()
				|| Robot.getRobotState().rightControlMode != rightOutput.getControlMode()) {
			Logger.getInstance().logSubsystemThread(Level.FINER, "Mismatched desired talon and actual talon states!");
			return false;
		}

		double positionTolerance = DrivetrainConstants.kAcceptableTurnAngleError * DrivetrainConstants.kDriveInchesPerDegree
				* DrivetrainConstants.kDriveTicksPerInch;
		double velocityTolerance = DrivetrainConstants.kAcceptableDriveVelocityError;

		if(cachedPose == null) {
			Logger.getInstance().logSubsystemThread(Level.FINER, "Cached pose is null");
			return false;
		}
		//System.out.println("Left: " + Math.abs(leftTarget - cachedPose.leftEnc) +
		//"Right: " + Math.abs(rightTarget - cachedPose.rightEnc));
		if(Math.abs(cachedPose.leftEncVelocity) < velocityTolerance && Math.abs(cachedPose.rightEncVelocity) < velocityTolerance
				&& Math.abs(leftTarget - cachedPose.leftEnc) < positionTolerance && Math.abs(rightTarget - cachedPose.rightEnc) < positionTolerance) {
			Logger.getInstance().logSubsystemThread(Level.FINER, "turn angle done");
			return true;
		} else
			return false;
	}

	@Override
	public DriveSignal update(RobotState state) {
		cachedPose = state.drivePose;
		return new DriveSignal(leftOutput, rightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(leftTarget, 0, 0, rightTarget, 0, 0, 0, 0);
	}

}
