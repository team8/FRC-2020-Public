package com.palyrobotics.frc2020.robot;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.control.DriveOutputs;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

/**
 * Commands represent what we want the robot to be doing.
 *
 * @author Nihar
 */
public class Commands {

	/* Routines */
	public List<RoutineBase> routinesWanted = new ArrayList<>();
	public boolean shouldClearCurrentRoutines;
	/* Climber */
	public Climber.ClimberState climberWantedState;
	private double climberWantedOutput;
	/* Drive */
	private Drive.DriveState driveWantedState;
	// Teleop
	private double driveWantedThrottle, driveWantedWheel;
	private boolean driveWantsQuickTurn, driveWantsBrake;
	// Signal
	private DriveOutputs driveWantedSignal;
	// Path Following
	private Trajectory driveWantedTrajectory;
	public Pose2d driveWantedOdometryPose;
	// Turning
	private double driveWantedHeadingDegrees;
	/* Indexer */
	public Indexer.IndexerState indexerWantedState;
	/* Intake */
	public Intake.IntakeState intakeWantedState;
	/* Shooter */
	private Shooter.ShooterState shooterWantedState;
	private double shooterManualWantedFlywheelVelocity;
	/* Spinner */
	public Spinner.SpinnerState spinnerWantedState;

	public void addWantedRoutines(RoutineBase... wantedRoutines) {
		for (RoutineBase wantedRoutine : wantedRoutines) {
			addWantedRoutine(wantedRoutine);
		}
	}

	/* Routines */
	public void addWantedRoutine(RoutineBase wantedRoutine) {
		routinesWanted.add(wantedRoutine);
	}

	/* Climber */
	public void setClimberWantedOutput(double output) {
		climberWantedOutput = output;
	}

	public double getClimberWantedOutput() {
		return climberWantedOutput;
	}

	/* Drive */
	public void setDriveSignal(DriveOutputs signal) {
		driveWantedState = Drive.DriveState.SIGNAL;
		driveWantedSignal = signal;
	}

	public void setDriveFollowPath(Trajectory trajectory) {
		driveWantedState = Drive.DriveState.FOLLOW_PATH;
		driveWantedTrajectory = trajectory;
	}

	public void setDriveVisionAlign() {
		driveWantedState = Drive.DriveState.VISION_ALIGN;
	}

	public void setDriveTeleop() {
		setDriveTeleop(0.0, 0.0, false, false);
	}

	public void setDriveTeleop(double throttle, double wheel, boolean wantsQuickTurn, boolean wantsBrake) {
		driveWantedState = Drive.DriveState.TELEOP;
		driveWantedThrottle = throttle;
		driveWantedWheel = wheel;
		driveWantsQuickTurn = wantsQuickTurn;
		driveWantsBrake = wantsBrake;
	}

	public void setDriveNeutral() {
		driveWantedState = Drive.DriveState.NEUTRAL;
	}

	public void setDriveTurn(double headingDegrees) {
		driveWantedState = Drive.DriveState.TURN;
		driveWantedHeadingDegrees = headingDegrees;
	}

	public Drive.DriveState getDriveWantedState() {
		return driveWantedState;
	}

	public boolean getDriveWantsQuickTurn() {
		return driveWantsQuickTurn;
	}

	public double getDriveWantedThrottle() {
		return driveWantedThrottle;
	}

	public double getDriveWantedWheel() {
		return driveWantedWheel;
	}

	public boolean getDriveWantsBreak() {
		return driveWantsBrake;
	}

	public Trajectory getDriveWantedTrajectory() {
		return driveWantedTrajectory;
	}

	public double getDriveWantedHeadingDegrees() {
		return driveWantedHeadingDegrees;
	}

	public DriveOutputs getDriveWantedSignal() {
		return driveWantedSignal;
	}

	/* Shooter */
	public void setShooterManualFlywheelVelocity(double wantedVelocity) {
		shooterWantedState = Shooter.ShooterState.MANUAL_VELOCITY;
		shooterManualWantedFlywheelVelocity = wantedVelocity;
	}

	public void setShooterVisionAssisted() {
		shooterWantedState = Shooter.ShooterState.VISION_VELOCITY;
	}

	public Shooter.ShooterState getShooterWantedState() {
		return shooterWantedState;
	}

	public double getShooterManualWantedFlywheelVelocity() {
		return shooterManualWantedFlywheelVelocity;
	}

	// public void copyTo(Commands other) {
	// }

	@Override
	public String toString() {
		var log = new StringBuilder();
		log.append("Wanted routines: ");
		for (RoutineBase routine : routinesWanted) {
			log.append(routine).append(" ");
		}
		return log.append("\n").toString();
	}

	public void reset() {
		climberWantedState = Climber.ClimberState.IDLE;
		driveWantedState = Drive.DriveState.NEUTRAL;
		indexerWantedState = Indexer.IndexerState.IDLE;
		intakeWantedState = Intake.IntakeState.INTAKE;
		shooterWantedState = Shooter.ShooterState.IDLE;
		spinnerWantedState = Spinner.SpinnerState.IDLE;
	}
}
