package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.palyrobotics.frc2020.auto.ShootThreeFriendlyTrenchThreeShootThree;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.indexer.IndexerFeedRoutine;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

/**
 * Used to produce {@link Commands}'s from human input. Should only be used in
 * robot package.
 *
 * @author Nihar
 */
public class OperatorInterface {

	private final Limelight mLimelight = Limelight.getInstance();
	private final Joystick mDriveStick = HardwareAdapter.Joysticks.getInstance().driveStick,
			mTurnStick = HardwareAdapter.Joysticks.getInstance().turnStick;
	private final XboxController mOperatorXboxController = HardwareAdapter.Joysticks
			.getInstance().operatorXboxController;
	private final List<Pose2d> kTestWaypoints = List.of(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)),
			new Pose2d(Units.inchesToMeters(200.0), 0.0, Rotation2d.fromDegrees(0.0)));

	/**
	 * Helper method to only add routines that aren't already in wantedRoutines
	 *
	 * @param commands      Current set of commands being modified
	 * @param wantedRoutine Routine to add to the commands
	 * @return whether or not wantedRoutine was successfully added
	 */
	private boolean addWantedRoutine(Commands commands, RoutineBase wantedRoutine) {
		for (RoutineBase routine : commands.routinesWanted) {
			if (routine.getClass().equals(wantedRoutine.getClass())) {
				return false;
			}
		}
		commands.routinesWanted.add(wantedRoutine);
		return true;
	}

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = false;

		updateClimberCommands(commands);
		updateDriveCommands(commands);
		updateIndexerCommands(commands, state);
		updateIntakeCommands(commands);
		updateSpinnerCommands(commands);

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		mOperatorXboxController.updateLastInputs();
	}

	private void updateClimberCommands(Commands commands) {
		ClimberConfig mConfig = Configs.get(ClimberConfig.class);
		double rightStick = -mOperatorXboxController.getY(GenericHID.Hand.kRight);
		if (Math.abs(rightStick) > 0.1) {
			commands.climberWantedState = Climber.ClimberState.CLIMBING;
			commands.setClimberWantedOutput(Math.abs(rightStick * mConfig.climbingMultiplier));
		} else {
			commands.climberWantedState = Climber.ClimberState.IDLE;
		}
		/* Adjusting */
		if (mDriveStick.getRawButton(3)) {
			commands.climberWantedState = Climber.ClimberState.ADJUSTING_LEFT;
		} else if (mDriveStick.getRawButton(4)) {
			commands.climberWantedState = Climber.ClimberState.ADJUSTING_RIGHT;
		} else {
			commands.climberWantedState = Climber.ClimberState.IDLE;
		}
	}

	private void updateDriveCommands(Commands commands) {
		boolean wantsAssistedVision = mTurnStick.getRawButton(3);
		if (wantsAssistedVision) {
			commands.setDriveVisionAlign();
		} else {
			commands.setDriveTeleop(-mDriveStick.getY(), mTurnStick.getX(), mTurnStick.getTrigger(),
					mDriveStick.getTrigger());
		}
		setVision(wantsAssistedVision);
		/* Path Following */
		if (mOperatorXboxController.getDPadUpPressed()) {
			commands.addWantedRoutine(new ShootThreeFriendlyTrenchThreeShootThree().getRoutine());
		} else if (mOperatorXboxController.getDPadRightPressed()) {
			commands.addWantedRoutine(new DrivePathRoutine(kTestWaypoints));
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.addWantedRoutine(new DrivePathRoutine(kTestWaypoints).reverse());
		}
	}

	private void updateIndexerCommands(Commands commands, @ReadOnly RobotState state) {
		if (mTurnStick.getRawButtonPressed(1)
				&& !(state.hasBackUltrasonicBall && state.hasFrontUltrasonicBall && state.hasTopUltrasonicBall)) {
			commands.indexerWantedState = Indexer.IndexerState.INDEX;
			commands.intakeWantedState = Intake.IntakeState.INTAKE;
		} else if (commands.shooterWantedState != Shooter.ShooterState.IDLE) {
			commands.indexerWantedState = Indexer.IndexerState.WAITING_TO_FEED;
			commands.intakeWantedState = Intake.IntakeState.IDLE;
		} else if (mTurnStick.getRawButtonPressed(2) && commands.shooterWantedState != Shooter.ShooterState.IDLE) {
			commands.addWantedRoutine(new IndexerFeedRoutine());
		} else {
			commands.indexerWantedState = Indexer.IndexerState.IDLE;
			commands.intakeWantedState = Intake.IntakeState.IDLE;
		}

		if (mTurnStick.getRawButtonPressed(4)) {
			commands.indexerWantedUpDownState = Indexer.IndexerUpDownState.UP;
		} else {
			commands.indexerWantedUpDownState = Indexer.IndexerUpDownState.DOWN;
		}
	}

	private void updateIntakeCommands(Commands commands) {
		if (mOperatorXboxController.getRightBumperPressed()) {
			commands.intakeWantedState = Intake.IntakeState.INTAKE;
		} else if (mOperatorXboxController.getLeftBumperPressed()) {
			commands.intakeWantedState = Intake.IntakeState.RAISE;
		}
	}

	private void updateSpinnerCommands(Commands commands) {
		// TODO Figure out better button
		if (mOperatorXboxController.getDPadRightPressed()) {
			commands.spinnerWantedState = Spinner.SpinnerState.POSITION_CONTROL;
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.spinnerWantedState = Spinner.SpinnerState.ROTATION_CONTROL;
		}
	}

	private void setVision(boolean on) {
		mLimelight.setCamMode(on ? LimelightControlMode.CamMode.VISION : LimelightControlMode.CamMode.DRIVER);
		mLimelight.setLEDMode(on ? LimelightControlMode.LedMode.FORCE_ON : LimelightControlMode.LedMode.FORCE_OFF);
	}
}
