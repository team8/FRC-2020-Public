package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SynchronousPID;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;

public class DriveStraightController implements DriveController {

	private Pose cachedPose;
	private double target;
	private Gains mGains;

	private SynchronousPID forwardPID;
	private SynchronousPID headingPID;

	private final double kTolerance;

	public DriveStraightController(Pose priorSetpoint, double distance) {
		target = (priorSetpoint.leftEnc + priorSetpoint.rightEnc) / 2 + (distance * Constants.kDriveTicksPerInch);
		Logger.getInstance().logSubsystemThread(Level.INFO, "Target", target);
		cachedPose = priorSetpoint;

		mGains = new Gains(.00035, 0.000004, 0.002, 0, 200, 0);
		kTolerance = Constants.kAcceptableDrivePositionError;
		forwardPID = new SynchronousPID(mGains.P, mGains.I, mGains.D, mGains.izone);
		headingPID = new SynchronousPID(Gains.kVidarDriveStraightTurnkP, 0, 0.005);
		forwardPID.setOutputRange(-1, 1);
		headingPID.setOutputRange(-0.2, 0.2);
		forwardPID.setSetpoint(target);
		headingPID.setSetpoint(priorSetpoint.heading);

	}

	@Override
	public boolean onTarget() {
		if(cachedPose == null) {
			Logger.getInstance().logSubsystemThread(Level.FINER, "Cached pose is null");
			return false;
		}

		return Math.abs(Robot.getRobotState().drivePose.heading) < kTolerance
				&& Math.abs((Robot.getRobotState().drivePose.leftEnc + Robot.getRobotState().drivePose.rightEnc) / 2 - target) < kTolerance
				&& Math.abs(Robot.getRobotState().drivePose.leftEncVelocity) < 0.05 && Math.abs(Robot.getRobotState().drivePose.rightEncVelocity) < 0.05;
	}

	@Override
	public DriveSignal update(RobotState state) {
		TalonSRXOutput leftOutput = new TalonSRXOutput();
		TalonSRXOutput rightOutput = new TalonSRXOutput();
		cachedPose = state.drivePose;
		double distanceSoFar = state.drivePose.leftEnc + state.drivePose.rightEnc;
		distanceSoFar /= 2;
		double throttle = forwardPID.calculate(distanceSoFar);
		//double turn = headingPID.calculate(state.drivePose.heading) * Constants.kDriveInchesPerDegree;
		double turn = 0;
		leftOutput.setPercentOutput(throttle + turn);
		rightOutput.setPercentOutput(throttle - turn);

		Logger.getInstance().logSubsystemThread(Level.FINEST, "Error", forwardPID.getError());
		return new DriveSignal(leftOutput, rightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(target, 0, 0, target, 0, 0, 0, 0);
	}

}
