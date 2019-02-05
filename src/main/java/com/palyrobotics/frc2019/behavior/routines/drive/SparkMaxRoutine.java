package com.palyrobotics.frc2019.behavior.routines.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.subsystems.controllers.SparkMaxDriveController;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;

/**
 * Created by Nihar on 2/12/17.
 * 
 * @author Nihar Should be used to set the drivetrain to an offboard closed loop cantalon
 */
public class SparkMaxRoutine extends Routine {
	private boolean relativeSetpoint = false;
	private final SparkSignal mSignal;

	private double timeout;
	private double startTime;
	private static RobotState robotState;

	public SparkMaxRoutine(SparkSignal controller, boolean relativeSetpoint) {
		this.mSignal = controller;
		this.timeout = 1 << 30;
		this.relativeSetpoint = relativeSetpoint;
		this.robotState = Robot.getRobotState();
        System.out.println("SparkMaxRoutine created, left setpoint: " + controller.leftMotor.getSetpoint());
        System.out.println("SparkMaxRoutine created, right setpoint: " + controller.rightMotor.getSetpoint());
	}

	/*
	 * Setpoint is relative when you want it to be updated on start For position and motion magic only
	 * 
	 * Timeout is in seconds
	 */
	public SparkMaxRoutine(SparkSignal controller, boolean relativeSetpoint, double timeout) {
		this.mSignal = controller;
		this.relativeSetpoint = relativeSetpoint;
		this.timeout = timeout * 1000;
		this.robotState = Robot.getRobotState();
	}

	@Override
	public void start() {

		startTime = System.currentTimeMillis();

		if(relativeSetpoint) {
			if(mSignal.leftMotor.getControlType().equals(ControlMode.Position)) {
				mSignal.leftMotor.setTargetPosition(mSignal.leftMotor.getSetpoint() + robotState.drivePose.leftEnc, mSignal.leftMotor.getGains());
				mSignal.rightMotor.setTargetPosition(mSignal.rightMotor.getSetpoint() + robotState.drivePose.rightEnc, mSignal.rightMotor.getGains());

			}
		}
		drive.setSparkMaxController(mSignal);
		Logger.getInstance().logRobotThread(Level.FINE, "Sent drivetrain signal", mSignal);
	}

	@Override
	public Commands update(Commands commands) {
		Commands output = commands.copy();
		output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		return output;
	}

	@Override
	public Commands cancel(Commands commands) {
		drive.setNeutral();
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
		return commands;
	}

	@Override
	public boolean finished() {
		//Wait for controller to be added before finishing routine
		if(Math.abs(mSignal.leftMotor.getSetpoint() - Robot.getRobotState().leftSetpoint) > 1) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired spark and actual spark setpoints! desired, actual");
			Logger.getInstance().logRobotThread(Level.WARNING, "Left", mSignal.leftMotor.getSetpoint() + ", " + Robot.getRobotState().leftSetpoint);
			return false;
		} else if(Math.abs(mSignal.rightMotor.getSetpoint() - Robot.getRobotState().rightSetpoint) > 1) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired spark and actual spark setpoints! desired, actual");
			Logger.getInstance().logRobotThread(Level.WARNING, "Right", mSignal.rightMotor.getSetpoint() + ", " + Robot.getRobotState().rightSetpoint);
			return false;
		} else if(mSignal.leftMotor.getControlType() != Robot.getRobotState().leftControlMode) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired spark and actual spark states!");
			Logger.getInstance().logRobotThread(Level.WARNING, mSignal.leftMotor.getControlType() + ", " + Robot.getRobotState().leftControlMode);
			return false;
		} else if(mSignal.rightMotor.getControlType() != Robot.getRobotState().rightControlMode) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired spark and actual spark states!");
			Logger.getInstance().logRobotThread(Level.WARNING, mSignal.rightMotor.getControlType() + ", " + Robot.getRobotState().rightControlMode);
			return false;
		}
		if(!drive.hasController() || (drive.getController().getClass() == SparkMaxDriveController.class && drive.controllerOnTarget())) {
		}

		return !drive.hasController() || System.currentTimeMillis() > this.timeout + startTime
				|| (drive.getController().getClass() == SparkMaxDriveController.class && drive.controllerOnTarget());
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { Drive.getInstance() };
	}

	@Override
	public String getName() {
		return "DriveCANSparkMaxRoutine";
	}
}
