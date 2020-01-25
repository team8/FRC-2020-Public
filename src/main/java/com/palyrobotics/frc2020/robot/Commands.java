package com.palyrobotics.frc2020.robot;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.control.DriveOutputs;

import edu.wpi.first.wpilibj.trajectory.Trajectory;

/**
 * Commands represent what we want the robot to be doing.
 *
 * @author Nihar
 */
public class Commands {

	private static Commands sInstance = new Commands();
	/* Routines */
	public List<RoutineBase> routinesWanted = new ArrayList<>();
	public boolean shouldClearCurrentRoutines;
	/* Climber Commands */
	public Climber.ClimberState climberWantedState;
	/* Drive Commands */
	private Drive.DriveState driveWantedState;
	/* Indexer Commands */
	public Indexer.IndexerState indexerWantedState;
	/* Intake Commands */
	public Intake.IntakeState intakeWantedState;
	/* Shooter Commands */
	public Shooter.ShooterState shooterWantedState;
	/* Spinner Commands */
	public Spinner.SpinnerState spinnerWantedState;
	// Teleop
	private double driveWantedThrottle, driveWantedWheel;
	private boolean driveWantsQuickTurn, driveWantsBrake;
	// Signal
	private DriveOutputs driveWantedSignal;
	// Path Following
	private Trajectory driveWantedTrajectory;
	private double driveWantedTrajectoryTimeSeconds;
	// Turning
	private double driveWantedHeading;
	// Climbing
	private double climberWantedOutput;

	public void addWantedRoutines(RoutineBase... wantedRoutines) {
		for (RoutineBase wantedRoutine : wantedRoutines) {
			addWantedRoutine(wantedRoutine);
		}
	}

	public void addWantedRoutine(RoutineBase wantedRoutine) {
		routinesWanted.add(wantedRoutine);
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

	public double getDriveWantedTrajectoryTimeSeconds() {
		return driveWantedTrajectoryTimeSeconds;
	}

	public double getDriveWantedHeading() {
		return driveWantedHeading;
	}

	public DriveOutputs getDriveWantedSignal() {
		return driveWantedSignal;
	}

	public double getClimberWantedOutput() {
		return climberWantedOutput;
	}

	public void setClimberWantedOutput(double output) {
		climberWantedOutput = output;
	}

	public void setDriveSignal(DriveOutputs signal) {
		driveWantedState = Drive.DriveState.SIGNAL;
		driveWantedSignal = signal;
	}

	public void setDriveFollowPath(Trajectory trajectory, double trajectoryTimeElapsedSeconds) {
		driveWantedState = Drive.DriveState.FOLLOW_PATH;
		driveWantedTrajectory = trajectory;
		driveWantedTrajectoryTimeSeconds = trajectoryTimeElapsedSeconds;
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

	public void setDriveTurn(double angle) {
		driveWantedState = Drive.DriveState.TURN;
		driveWantedHeading = angle;
	}

	public void copyTo(Commands other) {
		other.driveWantedState = driveWantedState;
		other.indexerWantedState = indexerWantedState;
		other.spinnerWantedState = spinnerWantedState;
		other.intakeWantedState = intakeWantedState;
		other.climberWantedState = climberWantedState;
		other.shouldClearCurrentRoutines = shouldClearCurrentRoutines;
		other.routinesWanted.addAll(routinesWanted);
	}

	@Override
	public String toString() {
		var log = new StringBuilder();
		log.append("Wanted routines: ");
		for (RoutineBase r : routinesWanted) {
			log.append(r.getName()).append(" ");
		}
		return log.append("\n").toString();
	}

	public void reset() {
		spinnerWantedState = Spinner.SpinnerState.IDLE;
		intakeWantedState = Intake.IntakeState.INTAKE;
		indexerWantedState = Indexer.IndexerState.IDLE;
		driveWantedState = Drive.DriveState.NEUTRAL;
	}
}
