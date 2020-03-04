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
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class Commands {

	/* Routines */
	public List<RoutineBase> routinesWanted = new ArrayList<>();
	public boolean shouldClearCurrentRoutines;
	/* Climber */
	public Climber.State climberWantedState;
	public double climberPositionSetpoint;
	public double climberWantedManualPercentOutput;
	public boolean climberWantsSoftLimits;
	/* Drive */
	/* Drive Commands */
	private Drive.State driveWantedState;
	// Teleop
	private double driveWantedThrottle, driveWantedWheel;
	private boolean driveWantsQuickTurn, driveWantsSlowTurn, driveWantedSlowTurnLeft, driveWantsBrake;
	// Signal
	private DriveOutputs driveWantedSignal;
	// Path Following
	private Trajectory driveWantedTrajectory;
	public Pose2d driveWantedOdometryPose;
	// Turning
	private double driveWantedYawDegrees;
	/* Indexer */
	public Indexer.BeltState indexerWantedBeltState;
	public Indexer.HopperState indexerWantedHopperState;
	public double indexerManualVelocity;
	/* Intake */
	public Intake.State intakeWantedState;
	public double intakeWantedPercentOutput;
	/* Shooter */
	private Shooter.ShooterState shooterWantedState;
	private double shooterWantedCustomFlywheelVelocity;
	/* Spinner */
	public Spinner.State spinnerWantedState;
	public double spinnerWantedPercentOutput;
	/* Vision */
	public int visionWantedPipeline;
	public boolean visionWanted;
	/* Miscellaneous */
	public boolean wantedCompression;
	/* Lighting */
	public Lighting.State lightingWantedState;

	public boolean wantedRumble;
	private Shooter.HoodState shooterWantedHoodState;

	public void addWantedRoutines(RoutineBase... wantedRoutines) {
		for (RoutineBase wantedRoutine : wantedRoutines) {
			addWantedRoutine(wantedRoutine);
		}
	}

	public void addWantedRoutine(RoutineBase wantedRoutine) {
		routinesWanted.add(wantedRoutine);
	}

	/* Drive */
	public void setDriveOutputs(DriveOutputs outputs) {
		driveWantedState = Drive.State.OUTPUTS;
		driveWantedSignal = outputs;
	}

	public void setDriveFollowPath(Trajectory trajectory) {
		driveWantedState = Drive.State.FOLLOW_PATH;
		driveWantedTrajectory = trajectory;
	}

	public void setDriveVisionAlign(int visionPipeline) {
		driveWantedState = Drive.State.VISION_ALIGN;
		visionWantedPipeline = visionPipeline;
		visionWanted = true;
	}

	public void setDriveTeleop() {
		setDriveTeleop(0.0, 0.0, false, false, false);
	}

	public void setDriveTeleop(double throttle, double wheel, boolean wantsQuickTurn, boolean wantsSlowTurn, boolean wantsBrake) {
		driveWantedState = Drive.State.TELEOP;
		driveWantedThrottle = throttle;
		driveWantedWheel = wheel;
		driveWantsQuickTurn = wantsQuickTurn;
		driveWantsSlowTurn = wantsSlowTurn;
		driveWantsBrake = wantsBrake;
	}

	public void setDriveNeutral() {
		driveWantedState = Drive.State.NEUTRAL;
	}

	public void setDriveYaw(double yawDegrees) {
		driveWantedState = Drive.State.TURN;
		driveWantedYawDegrees = yawDegrees;
	}

	public void setDriveSlowTurnLeft(boolean wantsSlowTurnLeft) {
		driveWantedSlowTurnLeft = wantsSlowTurnLeft;
	}

	public Drive.State getDriveWantedState() {
		return driveWantedState;
	}

	public boolean getDriveWantsQuickTurn() {
		return driveWantsQuickTurn;
	}

	public boolean getDriveWantsSlowTurn() {
		return driveWantsSlowTurn;
	}

	public boolean getDriveWantedSlowTurnLeft() {
		return driveWantedSlowTurnLeft;
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
		shooterWantedHoodState = Shooter.HoodState.HIGH;
	}

	public void setShooterCustomFlywheelVelocity(double wantedVelocity, Shooter.HoodState hoodState) {
		shooterWantedState = Shooter.ShooterState.CUSTOM_VELOCITY;
		shooterWantedCustomFlywheelVelocity = wantedVelocity;
		shooterWantedHoodState = hoodState;
	}

	public void setShooterFlywheelSpinUpVelocity(double wantedVelocity) {
		shooterWantedCustomFlywheelVelocity = wantedVelocity;
	}

	public void setShooterVisionAssisted(int visionPipeline) {
		shooterWantedState = Shooter.ShooterState.VISION_VELOCITY;
		visionWanted = true;
		visionWantedPipeline = visionPipeline;
	}

	public Shooter.ShooterState getShooterWantedState() {
		return shooterWantedState;
	}

	public double getShooterWantedCustomFlywheelVelocity() {
		return shooterWantedCustomFlywheelVelocity;
	}

	public Shooter.HoodState getShooterWantedHoodState() {
		return shooterWantedHoodState;
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
		other.indexerWantedBeltState = indexerWantedBeltState;
		other.spinnerWantedState = spinnerWantedState;
		other.intakeWantedState = intakeWantedState;
		other.shouldClearCurrentRoutines = shouldClearCurrentRoutines;
		other.routinesWanted.addAll(routinesWanted);
		other.climberWantedState = climberWantedState;
	}
}
