package com.palyrobotics.frc2020.robot;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.auto.StartLeftInitial180TrenchThree;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.miscellaneous.VibrateXboxRoutine;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.subsystems.Climber;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * Used to produce {@link Commands}'s from human input. Should only be used in
 * robot package.
 *
 * @author Nihar
 */
public class OperatorInterface {

	public static final double kDeadBand = 0.05;
	private final Limelight mLimelight = Limelight.getInstance();
	private final Joystick mDriveStick = HardwareAdapter.Joysticks.getInstance().driveStick,
			mTurnStick = HardwareAdapter.Joysticks.getInstance().turnStick;
	private final XboxController mOperatorXboxController = HardwareAdapter.Joysticks
			.getInstance().operatorXboxController;

	double climberOldVelocity;

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = false;

		updateClimberCommands(commands, state);
		updateDriveCommands(commands);
		updateIndexerCommands(commands);
		updateIntakeCommands(commands);
		updateSpinnerCommands(commands);

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		mOperatorXboxController.updateLastInputs();
	}

	private void updateClimberCommands(Commands commands, @ReadOnly RobotState state) {
		ClimberConfig mConfig = Configs.get(ClimberConfig.class);
		double velocityDiff = state.climberMedianVelocity - climberOldVelocity;

		switch (commands.climberWantedState) {
			case RAISING:
				if (Math.abs(state.climberPosition - mConfig.climberTopHeight) < mConfig.allowablePositionError
						&& mOperatorXboxController.getMenuButtonPressed()) {
					commands.climberWantedState = Climber.ClimberState.LOWERING_TO_BAR;
				}
				break;
			case LOWERING_TO_BAR:
				if (velocityDiff > mConfig.velocityChangeThreshold) {
					commands.climberWantedState = Climber.ClimberState.CLIMBING;
					commands.addWantedRoutine(new VibrateXboxRoutine(2.0));
				}
				break;
			case CLIMBING:
				commands.climberWantedVelocity = Util
						.handleDeadBand(mOperatorXboxController.getY(GenericHID.Hand.kLeft), kDeadBand);
				commands.climberWantedAdjustingPercentOutput = Util
						.handleDeadBand(mOperatorXboxController.getX(GenericHID.Hand.kRight), kDeadBand);
				break;
			default:
				if (mOperatorXboxController.getWindowButtonPressed()) {
					commands.climberWantedState = Climber.ClimberState.RAISING;
				} else if (mOperatorXboxController.getDPadUpPressed()) {
					if (commands.climberWantedState != Climber.ClimberState.LOCKED) {
						commands.preLockClimberWantedState = commands.climberWantedState;
						commands.climberWantedState = Climber.ClimberState.LOCKED;
					} else {
						commands.climberWantedState = commands.preLockClimberWantedState;
					}
				} else {
					commands.climberWantedState = Climber.ClimberState.IDLE;
				}
				break;
		}

		climberOldVelocity = state.climberMedianVelocity;
	}

	private void updateDriveCommands(Commands commands) {
		boolean wantsAssistedVision = mTurnStick.getRawButton(3);
		if (wantsAssistedVision) {
			commands.setDriveVisionAlign();
		} else {
			commands.setDriveTeleop(-mDriveStick.getY(), mTurnStick.getX(), mTurnStick.getTrigger(),
					mDriveStick.getTrigger());
		}
		// setVision(wantsAssistedVision);
		/* Path Following */
		if (mOperatorXboxController.getDPadUpPressed()) {
			// commands.addWantedRoutine(new
			// StartRightTrenchStealTwoShootFive().getRoutine());
			// commands.addWantedRoutine(new VisionAlignRoutine());
			commands.addWantedRoutine(new StartLeftInitial180TrenchThree().getRoutine());
		} else if (mOperatorXboxController.getDPadRightPressed()) {
			commands.addWantedRoutine(
					new SequentialRoutine(new DriveSetOdometryRoutine(0.0, 0.0, 0.0), new DriveYawRoutine(180.0)));
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.addWantedRoutine(new SequentialRoutine(new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
					new DrivePathRoutine(newWaypoint(100.0, 0.0, 0.0))));
		} else if (mOperatorXboxController.getDPadDownPressed()) {
			commands.addWantedRoutine(new DrivePathRoutine(newWaypoint(0.0, 0.0, 180.0)));
		}
	}

	private void updateIndexerCommands(Commands commands) {
		if (mTurnStick.getRawButtonPressed(3)) {
			commands.indexerWantedState = Indexer.IndexerState.INDEX;
		} else {
			commands.indexerWantedState = Indexer.IndexerState.IDLE;
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

	public void defaults(Commands commands) {
		commands.indexerWantedState = Indexer.IndexerState.IDLE;
		commands.setDriveNeutral();
		commands.intakeWantedState = Intake.IntakeState.INTAKE;
		commands.setShooterIdle();
		commands.spinnerWantedState = Spinner.SpinnerState.IDLE;
	}

	public void reset(Commands commands) {

	}
}
