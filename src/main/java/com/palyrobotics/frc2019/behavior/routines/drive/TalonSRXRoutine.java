package com.palyrobotics.frc2018.behavior.routines.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.config.dashboard.DashboardManager;
import com.palyrobotics.frc2018.robot.Robot;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.subsystems.Subsystem;
import com.palyrobotics.frc2018.subsystems.controllers.TalonSRXDriveController;
import com.palyrobotics.frc2018.util.DriveSignal;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

/**
 * Created by Nihar on 2/12/17.
 * 
 * @author Nihar Should be used to set the drivetrain to an offboard closed loop cantalon
 */
public class TalonSRXRoutine extends Routine {
	private boolean relativeSetpoint = false;
	private final DriveSignal mSignal;

	private double timeout;
	private double startTime;
	private static RobotState robotState;

	public TalonSRXRoutine(DriveSignal controller, boolean relativeSetpoint) {
		this.mSignal = controller;
		this.timeout = 1 << 30;
		this.relativeSetpoint = relativeSetpoint;
		this.robotState = Robot.getRobotState();
        System.out.println("TalonSRXRoutine created, left setpoint: " + controller.leftMotor.getSetpoint());
        System.out.println("TalonSRXRoutine created, right setpoint: " + controller.rightMotor.getSetpoint());
	}

	/*
	 * Setpoint is relative when you want it to be updated on start For position and motion magic only
	 * 
	 * Timeout is in seconds
	 */
	public TalonSRXRoutine(DriveSignal controller, boolean relativeSetpoint, double timeout) {
		this.mSignal = controller;
		this.relativeSetpoint = relativeSetpoint;
		this.timeout = timeout * 1000;
		this.robotState = Robot.getRobotState();
	}

	@Override
	public void start() {

		startTime = System.currentTimeMillis();

		if(relativeSetpoint) {
			if(mSignal.leftMotor.getControlMode().equals(ControlMode.MotionMagic)) {
				mSignal.leftMotor.setMotionMagic(mSignal.leftMotor.getSetpoint() + robotState.drivePose.leftEnc, mSignal.leftMotor.gains,
						mSignal.leftMotor.cruiseVel, mSignal.leftMotor.accel);
				mSignal.rightMotor.setMotionMagic(mSignal.rightMotor.getSetpoint() + robotState.drivePose.rightEnc, mSignal.rightMotor.gains,
						mSignal.rightMotor.cruiseVel, mSignal.rightMotor.accel);
			} else if(mSignal.leftMotor.getControlMode().equals(ControlMode.Position)) {
				mSignal.leftMotor.setPosition(mSignal.leftMotor.getSetpoint() + robotState.drivePose.leftEnc, mSignal.leftMotor.gains);
				mSignal.rightMotor.setPosition(mSignal.rightMotor.getSetpoint() + robotState.drivePose.rightEnc, mSignal.rightMotor.gains);

			}
		}
		drive.setTalonSRXController(mSignal);
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
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired talon and actual talon setpoints! desired, actual");
			Logger.getInstance().logRobotThread(Level.WARNING, "Left", mSignal.leftMotor.getSetpoint() + ", " + Robot.getRobotState().leftSetpoint);
			return false;
		} else if(Math.abs(mSignal.rightMotor.getSetpoint() - Robot.getRobotState().rightSetpoint) > 1) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired talon and actual talon setpoints! desired, actual");
			Logger.getInstance().logRobotThread(Level.WARNING, "Right", mSignal.rightMotor.getSetpoint() + ", " + Robot.getRobotState().rightSetpoint);
			return false;
		} else if(mSignal.leftMotor.getControlMode() != Robot.getRobotState().leftControlMode) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired talon and actual talon states!");
			Logger.getInstance().logRobotThread(Level.WARNING, mSignal.leftMotor.getControlMode() + ", " + Robot.getRobotState().leftControlMode);
			return false;
		} else if(mSignal.rightMotor.getControlMode() != Robot.getRobotState().rightControlMode) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Mismatched desired talon and actual talon states!");
			Logger.getInstance().logRobotThread(Level.WARNING, mSignal.rightMotor.getControlMode() + ", " + Robot.getRobotState().rightControlMode);
			return false;
		}
		if(!drive.hasController() || (drive.getController().getClass() == TalonSRXDriveController.class && drive.controllerOnTarget())) {
		}

		return !drive.hasController() || System.currentTimeMillis() > this.timeout + startTime
				|| (drive.getController().getClass() == TalonSRXDriveController.class && drive.controllerOnTarget());
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { Drive.getInstance() };
	}

	@Override
	public String getName() {
		return "DriveCANTalonRoutine";
	}
}
