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
	public Climber.ClimberState preLockClimberWantedState;
	private double climberWantedOutput;
	/* Drive */
	/* Drive Commands */
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
	private double driveWantedYawDegrees;
	/* Indexer */
	public Indexer.IndexerState indexerWantedState;
	public Indexer.IndexerUpDownState indexerWantedUpDownState;
	/* Intake */
	public Intake.IntakeState intakeWantedState;
	/* Shooter */
	private Shooter.ShooterState shooterWantedState;
	private double shooterManualWantedFlywheelVelocity;
	/* Spinner */
	public Spinner.SpinnerState spinnerWantedState;
	private double driveWantedHeadingDegrees;
	// Climbing
	public double climberWantedVelocity;
	public double climberWantedAdjustingPercentOutput;

	public boolean wantedRumble;

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

	public void setClimberWantedVelocity(double velocity) {
		climberWantedVelocity = velocity;
	}

	/* Drive */
	public void setClimberWantedAdjustingPercentOutput(double percentOutput) {
		climberWantedAdjustingPercentOutput = percentOutput;
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

	public void setDriveYaw(double yawDegrees) {
		driveWantedState = Drive.DriveState.TURN;
		driveWantedYawDegrees = yawDegrees;
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

	public double getDriveWantedYawDegrees() {
		return driveWantedYawDegrees;
	}

	public DriveOutputs getDriveWantedSignal() {
		return driveWantedSignal;
	}

	/* Shooter */
	public void setShooterIdle() {
		shooterWantedState = Shooter.ShooterState.IDLE;
	}

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

	public void copyTo(Commands other) {
		other.driveWantedState = driveWantedState;
		other.indexerWantedState = indexerWantedState;
		other.spinnerWantedState = spinnerWantedState;
		other.intakeWantedState = intakeWantedState;
		other.shouldClearCurrentRoutines = shouldClearCurrentRoutines;
		other.routinesWanted.addAll(routinesWanted);
		other.climberWantedState = climberWantedState;
		other.preLockClimberWantedState = preLockClimberWantedState;
		other.climberWantedAdjustingPercentOutput = climberWantedAdjustingPercentOutput;
		other.climberWantedVelocity = climberWantedVelocity;
	}

	public void reset() {
		spinnerWantedState = Spinner.SpinnerState.IDLE;
		intakeWantedState = Intake.IntakeState.INTAKE;
		indexerWantedState = Indexer.IndexerState.IDLE;
		shooterWantedState = Shooter.ShooterState.IDLE;
		driveWantedState = Drive.DriveState.NEUTRAL;
		wantedRumble = false;
	}
}
