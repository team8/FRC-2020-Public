package com.palyrobotics.frc2020.robot;

import java.util.ArrayList;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Spinner;
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
	public ArrayList<Routine> routinesWanted = new ArrayList<>();
	public boolean shouldClearCurrentRoutines;
	/* Spinner Commands */
	public Spinner.SpinnerState spinnerWantedState = Spinner.SpinnerState.IDLE;
	/* Intake Commands */
	public Intake.IntakeState intakeWantedState = Intake.IntakeState.IDLE;
	/* Indexer Commands */
	public Indexer.IndexerState indexerWantedState = Indexer.IndexerState.IDLE;
	/* Drive Commands */
	private Drive.DriveState driveWantedState = Drive.DriveState.NEUTRAL;
	// Teleop
	private double driveWantedThrottle, driveWantedWheel;
	private boolean driveWantsQuickTurn, driveWantsBrake;
	// Signal
	private DriveOutputs driveWantedSignal;
	// Path Following
	private Trajectory driveWantedTrajectory;
	// Turning
	private double driveWantedHeading;

	private Commands() {
	}

	public static Commands getInstance() {
		return sInstance;
	}

	static Commands resetInstance() {
		sInstance = new Commands();
		return sInstance;
	}

	public void addWantedRoutines(Routine... wantedRoutines) {
		for (Routine wantedRoutine : wantedRoutines) {
			addWantedRoutine(wantedRoutine);
		}
	}

	public void addWantedRoutine(Routine wantedRoutine) {
		routinesWanted.add(wantedRoutine);
	}

	public Drive.DriveState getDriveState() {
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

	public Trajectory getDriveTrajectory() {
		return driveWantedTrajectory;
	}

	public double getDriveWantedHeading() {
		return driveWantedHeading;
	}

	public DriveOutputs getWantedDriveSignal() {
		return driveWantedSignal;
	}

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

	public void setDriveTeleop(double driveThrottle, double driveWheel, boolean driveWantsQuickTurn,
			boolean driveWantsBrake) {
		driveWantedState = Drive.DriveState.TELEOP;
		this.driveWantedThrottle = driveThrottle;
		this.driveWantedWheel = driveWheel;
		this.driveWantsQuickTurn = driveWantsQuickTurn;
		this.driveWantsBrake = driveWantsBrake;
	}

	public void setDriveNeutral() {
		driveWantedState = Drive.DriveState.NEUTRAL;
	}

	public void setDriveTurn(double angle) {
		driveWantedState = Drive.DriveState.TURN;
		driveWantedHeading = angle;
	}

	public void copyTo(Commands other) {
		other.driveWantedState = this.driveWantedState;
		other.indexerWantedState = this.indexerWantedState;
		other.spinnerWantedState = this.spinnerWantedState;
		other.intakeWantedState = this.intakeWantedState;
		other.shouldClearCurrentRoutines = this.shouldClearCurrentRoutines;
		other.routinesWanted.addAll(this.routinesWanted);
	}

	@Override
	public String toString() {
		StringBuilder log = new StringBuilder();
		log.append("Wanted routines: ");
		for (Routine r : routinesWanted) {
			log.append(r.getName()).append(" ");
		}
		return log.append("\n").toString();
	}
}
