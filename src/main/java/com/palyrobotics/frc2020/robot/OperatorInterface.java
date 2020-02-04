package com.palyrobotics.frc2020.robot;

import static com.palyrobotics.frc2020.util.Util.handleDeadBand;
import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.auto.StartLeftInitial180TrenchThree;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.indexer.IndexerFeedSingleRoutine;
import com.palyrobotics.frc2020.behavior.routines.indexer.IndexerTimeRoutine;
import com.palyrobotics.frc2020.behavior.routines.miscellaneous.XboxVibrateRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerPositionControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerRotationControlRoutine;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.robot.HardwareAdapter.Joysticks;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;

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
	private final Joystick mDriveStick = Joysticks.getInstance().driveStick,
			mTurnStick = Joysticks.getInstance().turnStick;
	private final XboxController mOperatorXboxController = Joysticks.getInstance().operatorXboxController;

	double climberLastVelocity;

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = false;

		updateClimberCommands(commands, state);
		updateDriveCommands(commands);
		updateBallSuperstructure(commands, state);
		updateSpinnerCommands(commands);

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
						commands.addWantedRoutine(new XboxVibrateRoutine(2.0));
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
					new SequentialRoutine(
							new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
							new DriveYawRoutine(180.0)));
		} else if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.addWantedRoutine(
					new SequentialRoutine(
							new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
							new DrivePathRoutine(newWaypoint(100.0, 0.0, 0.0))));
		} else if (mOperatorXboxController.getDPadDownPressed()) {
			commands.addWantedRoutine(
					new DrivePathRoutine(newWaypoint(0.0, 0.0, 180.0)));
		}
	}

	private void updateBallSuperstructure(Commands commands, @ReadOnly RobotState state) {
		/* Intake Toggle */
		if (mOperatorXboxController.getDPadDownPressed()) {
			commands.intakeWantedState = state.intakeIsExtended ? Intake.State.STOW : Intake.State.LOWER;
		}
		/* Indexer Hopper Control */
		if (mOperatorXboxController.getDPadRightPressed()) {
			if (state.indexerIsHopperExtended) {
				// Open hopper, stow intake, and advance balls
				commands.indexerWantedHopperState = Indexer.HopperState.OPEN;
				commands.intakeWantedState = Intake.State.STOW;
				commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
			} else {
				// Close hopper, lower intake, and advance balls a bit
				commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
				commands.addWantedRoutine(new IndexerTimeRoutine(1.0));
			}
		}
		/* Ball Intake Control */
		if (mOperatorXboxController.getDPadLeft()) {
			commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
			commands.intakeWantedState = Intake.State.INTAKE;
			commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
		}
		if (mOperatorXboxController.getDPadLeftReleased()) {
			commands.intakeWantedState = Intake.State.LOWER;
			commands.addWantedRoutine(new IndexerTimeRoutine(1.0));
		}
		/* Shooting */
		// Handle flywheel velocity
		if (mOperatorXboxController.getRightTriggerPressed()) {
			commands.setShooterVisionAssisted();
		} else if (mOperatorXboxController.getLeftTriggerPressed()) {
			commands.setShooterIdle();
		}
		// Feeding
		if (state.shooterIsReadyToShoot) {
			if (mOperatorXboxController.getLeftBumperPressed()) {
				// Shoot one ball
				commands.addWantedRoutine(new IndexerFeedSingleRoutine());
			} else if (mOperatorXboxController.getRightBumperPressed()) {
				commands.addWantedRoutine(new IndexerFeedAllRoutine());
			}
		}
	}

	private void updateSpinnerCommands(Commands commands) {
		if (mOperatorXboxController.getAButtonPressed()) {
			commands.addWantedRoutine(new SpinnerRotationControlRoutine());
		} else if (mOperatorXboxController.getYButtonPressed()) {
			commands.addWantedRoutine(new SpinnerPositionControlRoutine());
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
