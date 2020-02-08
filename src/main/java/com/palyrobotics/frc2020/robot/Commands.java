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
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class Commands {

	/* Routines */
	public List<RoutineBase> routinesWanted = new ArrayList<>();
	public boolean shouldClearCurrentRoutines;
	/* Climber */
	public Climber.ClimberState climberWantedState;
	public Climber.ClimberState preLockClimberWantedState;
	public double climberWantedVelocity;
	public double climberWantedAdjustingPercentOutput;
	/* Drive */
	/* Drive Commands */
	private Drive.State driveWantedState;
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
	public Indexer.BeltState indexerWantedBeltState;
	public Indexer.HopperState indexerWantedHopperState;
	/* Intake */
	public Intake.State intakeWantedState;
	/* Shooter */
	private Shooter.ShooterState shooterWantedState;
	private double shooterWantedCustomFlywheelVelocity;
	/* Turret */
	public Turret.TurretState turretWantedState;
	public boolean turretCalibrationWanted;
	/* Spinner */
	public Spinner.State spinnerWantedState;
	/* Vision */
	public int visionWantedPipeline;
	public boolean visionWanted;
	/* Miscellaneous */
	public boolean wantedCompression;

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

	/* Climber */
	public void setClimberWantedOutput(double output) {
	}

	public void setClimberWantedVelocity(double velocity) {
		climberWantedVelocity = velocity;
	}

	/* Drive */
	public void setClimberWantedAdjustingPercentOutput(double percentOutput) {
		climberWantedAdjustingPercentOutput = percentOutput;
	}

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
		setDriveTeleop(0.0, 0.0, false, false);
	}

	public void setDriveTeleop(double throttle, double wheel, boolean wantsQuickTurn, boolean wantsBrake) {
		driveWantedState = Drive.State.TELEOP;
		driveWantedThrottle = throttle;
		driveWantedWheel = wheel;
		driveWantsQuickTurn = wantsQuickTurn;
		driveWantsBrake = wantsBrake;
	}

	public void setDriveNeutral() {
		driveWantedState = Drive.State.NEUTRAL;
	}

	public void setDriveYaw(double yawDegrees) {
		driveWantedState = Drive.State.TURN;
		driveWantedYawDegrees = yawDegrees;
	}

	public Drive.State getDriveWantedState() {
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
		shooterWantedHoodState = Shooter.HoodState.HIGH;
	}

	public void setShooterCustomFlywheelVelocity(double wantedVelocity, Shooter.HoodState hoodState) {
		shooterWantedState = Shooter.ShooterState.CUSTOM_VELOCITY;
		shooterWantedCustomFlywheelVelocity = wantedVelocity;
		shooterWantedHoodState = hoodState;
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
		other.preLockClimberWantedState = preLockClimberWantedState;
		other.climberWantedAdjustingPercentOutput = climberWantedAdjustingPercentOutput;
		other.climberWantedVelocity = climberWantedVelocity;
	}
}
