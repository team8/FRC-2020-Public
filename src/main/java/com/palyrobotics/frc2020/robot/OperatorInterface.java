package com.palyrobotics.frc2020.robot;

import static com.palyrobotics.frc2020.util.Util.handleDeadBand;
import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.auto.StartLeftInitial180TrenchThree;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.indexer.IndexerFeedRoutine;
import com.palyrobotics.frc2020.behavior.routines.indexer.IndexerTimeRoutine;
import com.palyrobotics.frc2020.behavior.routines.miscellaneous.VibrateXboxRoutine;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * Used to produce {@link Commands}'s from human input. Should only be used in robot package.
 *
 * @author Nihar
 */
public class OperatorInterface {

	public static final double kDeadBand = 0.05;
	public static final int kClimberEnableControlTimeSeconds = 30;
	private final Limelight mLimelight = Limelight.getInstance();
	private final Joystick mDriveStick = HardwareAdapter.Joysticks.getInstance().driveStick,
			mTurnStick = HardwareAdapter.Joysticks.getInstance().turnStick;
	private final XboxController mOperatorXboxController = HardwareAdapter.Joysticks
			.getInstance().operatorXboxController;

	double climberLastVelocity;

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = false;

		updateClimberCommands(commands, state);
		updateDriveCommands(commands);
		updateBallSuperstructure(commands, state);

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		mOperatorXboxController.updateLastInputs();
	}

	private void updateClimberCommands(Commands commands, @ReadOnly RobotState state) {
		if (DriverStation.getInstance().getMatchTime() < kClimberEnableControlTimeSeconds) {
			var mConfig = Configs.get(ClimberConfig.class);
			double velocityDelta = state.climberVelocity - climberLastVelocity;

			switch (commands.climberWantedState) {
				case RAISING:
					if (Util.withinRange(state.climberPosition, mConfig.climberTopHeight,
							mConfig.allowablePositionError) && mOperatorXboxController.getMenuButtonPressed()) {
						commands.climberWantedState = Climber.ClimberState.LOWERING_TO_BAR;
					}
					break;
				case LOWERING_TO_BAR:
					if (velocityDelta > mConfig.velocityChangeThreshold) {
						commands.climberWantedState = Climber.ClimberState.CLIMBING;
						commands.addWantedRoutine(new VibrateXboxRoutine(2.0));
					}
					break;
				case CLIMBING:
					commands.climberWantedVelocity = handleDeadBand(mOperatorXboxController.getY(Hand.kLeft),
							kDeadBand);
					commands.climberWantedAdjustingPercentOutput = handleDeadBand(
							mOperatorXboxController.getX(Hand.kRight), kDeadBand);
					break;
			}

			// Raise from anywhere if you're not climbing or locked
			if (mOperatorXboxController.getWindowButtonPressed()) {
				if (commands.climberWantedState == Climber.ClimberState.LOWERING_TO_BAR ||
						commands.climberWantedState == Climber.ClimberState.IDLE) {
					commands.climberWantedState = Climber.ClimberState.RAISING;
				}
			} else if (mOperatorXboxController.getDPadUpPressed()) {
				// Toggle climber lock
				if (commands.climberWantedState == Climber.ClimberState.LOCKED) {
					commands.climberWantedState = commands.preLockClimberWantedState;
				} else {
					commands.preLockClimberWantedState = commands.climberWantedState;
					commands.climberWantedState = Climber.ClimberState.LOCKED;
				}
			}

			climberLastVelocity = state.climberVelocity;
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

	private void updateBallSuperstructure(Commands commands, @ReadOnly RobotState state) {
		if (mOperatorXboxController.getDPadRightPressed()) {
			if (state.indexerIsHoppedExtended) {
				commands.indexerWantedUpDownState = Indexer.HopperState.OPEN;
			} else {
				commands.indexerWantedUpDownState = Indexer.HopperState.CLOSED;
				commands.addWantedRoutine(new IndexerTimeRoutine(1));
			}
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
			commands.indexerWantedUpDownState = Indexer.HopperState.CLOSED;
		}

		if (mOperatorXboxController.getDPadLeftReleased()) {
			commands.addWantedRoutine(new IndexerTimeRoutine(1));
		}

		if (mOperatorXboxController.getLeftBumperPressed() &&
				commands.getShooterWantedState() != Shooter.ShooterState.IDLE) { // TODO: Check speed with a new boolean in robot state
			commands.addWantedRoutine(new IndexerFeedRoutine());
		} else if (mOperatorXboxController.getRightBumperPressed() &&
				commands.getShooterWantedState() != Shooter.ShooterState.IDLE) { // TODO: Check speed with a new boolean in robot state
					commands.indexerWantedBeltState = Indexer.BeltState.FEED_ALL;
				}

		if (mOperatorXboxController.getDPadRightPressed()) {
			if (Indexer.getInstance().getHopperOutput()) {
				commands.intakeWantedState = Intake.State.RAISE;
			} else {
				commands.intakeWantedState = Intake.State.INTAKE;
			}
		} else if (mOperatorXboxController.getDPadDownPressed()) {
			if (state.intakeIsExtended) {
				commands.intakeWantedState = Intake.State.RAISE;
			} else {
				commands.intakeWantedState = Intake.State.LOWER;
			}
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.intakeWantedState = Intake.State.INTAKE;
		}
		if (mOperatorXboxController.getRightTriggerPressed()) {
			commands.setShooterVisionAssisted();
		} else if (mOperatorXboxController.getLeftTriggerPressed()) {
			commands.setShooterIdle();
		}
		// TODO Figure out better button
		if (mOperatorXboxController.getDPadRightPressed()) {
			commands.spinnerWantedState = Spinner.State.POSITION_CONTROL;
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.spinnerWantedState = Spinner.State.ROTATION_CONTROL;
		}
	}

	public void reset(Commands commands) {
		commands.climberWantedState = Climber.ClimberState.IDLE;
		commands.setDriveNeutral();
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
		commands.intakeWantedState = Intake.State.INTAKE;
		commands.setShooterIdle();
		commands.spinnerWantedState = Spinner.State.IDLE;
	}
}
