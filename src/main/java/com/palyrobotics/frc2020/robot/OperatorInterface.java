package com.palyrobotics.frc2020.robot;

import static com.palyrobotics.frc2020.util.Util.handleDeadBand;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;
import static com.palyrobotics.frc2020.vision.Limelight.kTwoTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.routines.miscellaneous.XboxVibrateRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerPositionControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerRotationControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedSingleRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerIdleRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerTimeRoutine;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.HardwareAdapter.Joysticks;
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
 */
public class OperatorInterface {

	public static final double kDeadBand = 0.05;
	public static final double kClimberEnableControlTimeSeconds = 30;
	public static final int kOnesTimesZoomAlignRawButton = 3, kTwoTimesZoomAlignButton = 4;
	private final Joystick mDriveStick = Joysticks.getInstance().driveStick,
			mTurnStick = Joysticks.getInstance().turnStick;
	private final XboxController mOperatorXboxController = Joysticks.getInstance().operatorXboxController;
	private final Limelight mLimelight = Limelight.getInstance();
	private double mClimberLastVelocity;

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		updateClimberCommands(commands, state);
		updateDriveCommands(commands);
		updateSuperstructure(commands, state);
//		updateSpinnerCommands(commands);
		updateLightingCommands(commands);

		// TODO: remove
		if (mOperatorXboxController.getAButtonPressed()) {
			var customVelocity = Configs.get(ShooterConfig.class).customVelocity;
			commands.setShooterCustomFlywheelVelocity(customVelocity, Shooter.HoodState.MIDDLE);
		}

		// TODO: remove
		if (mTurnStick.getRawButtonPressed(6)) {
			commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
		}

		// TODO: remove
		if (mTurnStick.getRawButtonReleased(6)) {
			commands.indexerWantedBeltState = Indexer.BeltState.WAITING_TO_FEED;
		}

		mOperatorXboxController.updateLastInputs();
	}

	private void updateClimberCommands(Commands commands, @ReadOnly RobotState state) {
		if (DriverStation.getInstance().getMatchTime() < kClimberEnableControlTimeSeconds) {
			var mConfig = Configs.get(ClimberConfig.class);
			double velocityDelta = state.climberVelocity - mClimberLastVelocity;

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

			mClimberLastVelocity = state.climberVelocity;
		}
	}

	private void updateDriveCommands(Commands commands) {
		// Both buttons align, button 3: 2x zoom, button 4: 1x zoom
		boolean wantsOneTimesAlign = mTurnStick.getRawButton(kOnesTimesZoomAlignRawButton),
				wantsTwoTimesAlign = mTurnStick.getRawButton(kTwoTimesZoomAlignButton);
		if (wantsOneTimesAlign) {
			commands.setDriveVisionAlign(kTwoTimesZoomPipelineId);
		} else if (wantsTwoTimesAlign) {
			commands.setDriveVisionAlign(kOneTimesZoomPipelineId);
		} else {
			commands.setDriveTeleop(
					-mDriveStick.getY(), mTurnStick.getX(),
					mTurnStick.getTrigger(), mDriveStick.getTrigger());
		}
		if (mTurnStick.getRawButtonReleased(kOnesTimesZoomAlignRawButton) || mTurnStick.getRawButtonReleased(kTwoTimesZoomAlignButton)) {
			commands.visionWanted = false;
		}
		/* Path Following */
//		if (mOperatorXboxController.getDPadDownPressed()) {
//			commands.addWantedRoutines(
//					new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
//					new DrivePathRoutine(newWaypoint(30.0, 0.0, 0.0)));
//		}
	}

	private void updateLightingCommands(Commands commands) {
		// TODO: temporary, remove
		if (mTurnStick.getRawButtonPressed(6)) {
			commands.lightingWantedState = Lighting.State.TARGET_FOUND;
		}
		if (mTurnStick.getRawButtonPressed(5)) {
			commands.lightingWantedState = Lighting.State.INIT;
		}
		if (mTurnStick.getRawButtonPressed(4)) {
			commands.lightingWantedState = Lighting.State.SHOOTER_FULLRPM;
			//Lighting.getInstance().tempBallCount += 1;
		}
		if (mTurnStick.getRawButtonPressed(3)) {
			commands.lightingWantedState = Lighting.State.DISABLE;
		}
	}

	private void updateSuperstructure(Commands commands, @ReadOnly RobotState state) {
		/* Intake Toggle */
		if (mOperatorXboxController.getDPadDownPressed()) {
			switch (commands.intakeWantedState) {
				case LOWER:
				case INTAKE:
					commands.intakeWantedState = Intake.State.STOW;
					break;
				case STOW:
					commands.intakeWantedState = Intake.State.LOWER;
					break;
			}
		}
		/* Indexer Hopper Control */
		if (mOperatorXboxController.getDPadRightPressed()) {
			if (commands.indexerWantedHopperState == Indexer.HopperState.CLOSED) {
				// Open hopper, stow intake, and advance balls
				commands.indexerWantedHopperState = Indexer.HopperState.OPEN;
				commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
			} else if (commands.indexerWantedHopperState == Indexer.HopperState.OPEN) {
				// Close hopper, lower intake, and advance balls a bit
				commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
				commands.addWantedRoutine(new IndexerTimeRoutine(1.0));
			}
			commands.intakeWantedState = Intake.State.LOWER;
		}
		/* Ball Intake Control */
		if (mOperatorXboxController.getDPadLeft()) {
			commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
			commands.intakeWantedState = Intake.State.INTAKE;
			commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
		}
		if (mOperatorXboxController.getDPadLeftReleased()) {
			commands.intakeWantedState = Intake.State.LOWER;
			commands.addWantedRoutine(new IndexerTimeRoutine(0.5));
		}
		/* Shooting */
		// Handle flywheel velocity
		if (mOperatorXboxController.getRightTriggerPressed()) {
			commands.setShooterVisionAssisted(commands.visionWantedPipeline);
			commands.wantedCompression = false;
		} else if (mOperatorXboxController.getLeftTriggerPressed()) {
			commands.setShooterIdle();
			commands.visionWanted = false;
			commands.addWantedRoutine(new IndexerIdleRoutine());
			commands.wantedCompression = true;
		}
		// Feeding
		if (mOperatorXboxController.getLeftBumperPressed()) {
			// Shoot one ball
			commands.addWantedRoutine(new IndexerFeedSingleRoutine());
		} else if (mOperatorXboxController.getRightBumperPressed()) {
			commands.addWantedRoutine(new IndexerFeedAllRoutine());
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
		commands.routinesWanted.clear();
		commands.climberWantedState = Climber.ClimberState.IDLE;
		commands.setDriveNeutral();
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
		commands.intakeWantedState = Intake.State.STOW;
		commands.indexerWantedHopperState = Indexer.HopperState.OPEN;
		commands.lightingWantedState = Lighting.State.INIT;
		commands.setShooterIdle();
		commands.spinnerWantedState = Spinner.State.IDLE;
		commands.wantedCompression = true;
		commands.wantedRumble = false;
		commands.visionWanted = false;
		mOperatorXboxController.clearLastInputs();
	}
}
