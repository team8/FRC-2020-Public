package com.palyrobotics.frc2020.robot;

import static com.palyrobotics.frc2020.util.Util.handleDeadBand;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;
import static com.palyrobotics.frc2020.vision.Limelight.kTwoTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerPositionControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerRotationControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedSingleRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerIdleRoutine;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.HardwareAdapter.Joysticks;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * Used to produce {@link Commands}'s from human input. Should only be used in robot package.
 */
public class OperatorInterface {

	public static final double kDeadBand = 0.05;
	public static final int kOnesTimesZoomAlignButton = 3, kTwoTimesZoomAlignButton = 4;
	private final ShooterConfig mShooterConfig = Configs.get(ShooterConfig.class);
	private final IntakeConfig mIntakeConfig = Configs.get(IntakeConfig.class);
	private final Joystick mDriveStick = Joysticks.getInstance().driveStick,
			mTurnStick = Joysticks.getInstance().turnStick;
	private Limelight mLimelight = Limelight.getInstance();
	private final XboxController mOperatorXboxController = Joysticks.getInstance().operatorXboxController;

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		updateClimberCommands(commands);
		updateDriveCommands(commands);
		updateSuperstructure(commands, state);
		updateSpinnerCommands(commands);
		updateLightingCommands(commands, state);
		mOperatorXboxController.updateLastInputs();
	}

	private void updateClimberCommands(Commands commands) {

		if (mOperatorXboxController.getDPadUpPressed()) {
			if (commands.climberWantedState != Climber.State.LOCKED) {
				commands.climberWantedState = Climber.State.LOCKED;
			} else {
				commands.climberWantedState = Climber.State.IDLE;
			}
		}

		commands.climberWantedManualPercentOutput = -mOperatorXboxController.getY(Hand.kLeft);

		if (mOperatorXboxController.getStickButtonPressed(Hand.kLeft)) {
			commands.climberWantsSoftLimits = false;
		} else if (mOperatorXboxController.getStickButtonReleased(Hand.kLeft)) {
			commands.climberWantsSoftLimits = true;
		}

		if (commands.climberWantedState != Climber.State.MANUAL && handleDeadBand(commands.climberWantedManualPercentOutput, kDeadBand) != 0) {
			commands.climberWantedState = Climber.State.MANUAL;
		} else if (commands.climberWantedState == Climber.State.IDLE && handleDeadBand(commands.climberWantedManualPercentOutput, kDeadBand) == 0) {
			commands.climberWantedState = Climber.State.IDLE;
		}
	}

	private void updateDriveCommands(Commands commands) {
		commands.setDriveSlowTurnLeft(mTurnStick.getPOV(0) == 270);
		commands.setDriveTeleop(
				handleDeadBand(-mDriveStick.getY(), kDeadBand), handleDeadBand(mTurnStick.getX(), kDeadBand),
				mTurnStick.getTrigger(), mTurnStick.getPOV(0) == 90 || mTurnStick.getPOV(0) == 270,
				mDriveStick.getTrigger());
		boolean wantsOneTimesAlign = mTurnStick.getRawButton(kOnesTimesZoomAlignButton),
				wantsTwoTimesAlign = mTurnStick.getRawButton(kTwoTimesZoomAlignButton);
		// Vision align overwrites wanted drive state, using teleop commands when no target is in sight
		if (wantsOneTimesAlign) {
			commands.setDriveVisionAlign(kOneTimesZoomPipelineId);
		} else if (wantsTwoTimesAlign) {
			commands.setDriveVisionAlign(kTwoTimesZoomPipelineId);
		}
		boolean justReleasedAlign = mTurnStick.getRawButtonReleased(kOnesTimesZoomAlignButton) || mTurnStick.getRawButtonReleased(kTwoTimesZoomAlignButton);
		if (justReleasedAlign && commands.getShooterWantedState() != Shooter.ShooterState.VISION_VELOCITY) {
			commands.visionWanted = false;
		}
		/* Path Following */
//		if (mOperatorXboxController.getBButtonPressed()) {
//			commands.addWantedRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
//					new DrivePathRoutine(newWaypoint(30.0, 0.0, 0.0))));
//			commands.addWantedRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(0.0, 0.0, 180.0),
//					new DriveYawRoutine(0.0)));
//			commands.addWantedRoutine(new DrivePathRoutine(newWaypoint(0.0, 0.0, 180.0)));
//			commands.addWantedRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
//					new DrivePathRoutine(newWaypoint(40.0, 0.0, 0.0))
//							.setMovement(1.0, 1.0)
//							.endingVelocity(0.5),
//					new DrivePathRoutine(newWaypoint(80.0, 0.0, 0.0))
//							.setMovement(0.5, 1.0)
//							.startingVelocity(0.5)));
//		}
	}

	private void updateLightingCommands(Commands commands, @ReadOnly RobotState state) {

		if (mOperatorXboxController.getDPadLeftPressed()) {
			commands.lightingWantedState = Lighting.State.INTAKE_EXTENDED; // TODO: Dpad left isn't for intake_extended
		}
		if (state.indexerHasBackBall) {
			commands.lightingWantedState = Lighting.State.BALL_ENTERED;
		}
		if (mLimelight.isTargetFound()) {
			commands.lightingWantedState = Lighting.State.TARGET_FOUND;
		}

		if (mTurnStick.getRawButton(3) || mTurnStick.getRawButton(4) && mLimelight.isAligned()) {
			commands.lightingWantedState = Lighting.State.ROBOT_ALIGNED;
		}

		if (state.indexerHasTopBall) {
			commands.lightingWantedState = Lighting.State.BALL_SHOT;
		}

//		if (state.shooterIsReadyToShoot) {
//			commands.lightingWantedState = Lighting.State.SHOOTER_FULLRPM;
//		}

		if (commands.climberWantedState == Climber.State.LOCKED) {
			commands.lightingWantedState = Lighting.State.CLIMB_DONE;
		}

		// Checks for limelight connection
		if (mLimelight.isConnected()) {
			commands.lightingWantedState = Lighting.State.LIMELIGHT_RESTART;
		}

		if (!state.driveIsGyroReady) {
			commands.lightingWantedState = Lighting.State.PIGEON_DISCONNECT;
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
			}
			commands.intakeWantedState = Intake.State.LOWER;
		}

		if (mOperatorXboxController.getAButtonPressed()) {
			commands.setShooterCustomFlywheelVelocity(mShooterConfig.customVelocity, Shooter.HoodState.LOW);
		}
		/* Ball Intake Control */
		if (mOperatorXboxController.getDPadLeft()) {
			commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
			commands.intakeWantedState = Intake.State.INTAKE;
			commands.intakeWantedPercentOutput = mIntakeConfig.intakingOutput;
			commands.indexerWantedBeltState = Indexer.BeltState.INDEX;
		}
		if (mOperatorXboxController.getDPadLeftReleased()) {
			commands.intakeWantedState = Intake.State.LOWER;
			commands.intakeWantedPercentOutput = 0.0;
			commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
		}
		/* Shooting */
		// Handle flywheel velocity
		if (mOperatorXboxController.getRightTriggerPressed()) {
			commands.setShooterFlywheelSpinUpVelocity(mShooterConfig.noTargetSpinUpVelocity);
			commands.setShooterVisionAssisted(commands.visionWantedPipeline);
			commands.indexerWantedHopperState = Indexer.HopperState.OPEN;
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
			commands.addWantedRoutine(new IndexerFeedAllRoutine(10.0, false, true));
		}
		double indexerManualOutput = handleDeadBand(mOperatorXboxController.getY(Hand.kRight), 0.1);
		if (Math.abs(indexerManualOutput) > 0.1) {
			commands.indexerWantedBeltState = Indexer.BeltState.MANUAL;
			commands.indexerManualVelocity = -indexerManualOutput * Configs.get(IndexerConfig.class).feedingOutput;
		} else if (commands.indexerWantedBeltState == Indexer.BeltState.MANUAL) {
			commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
		}
	}

	private void updateSpinnerCommands(Commands commands) {
		if (mOperatorXboxController.getWindowButtonPressed()) {
			commands.addWantedRoutine(new SpinnerRotationControlRoutine());
		} else if (mOperatorXboxController.getMenuButtonPressed()) {
			commands.addWantedRoutine(new SpinnerPositionControlRoutine());
		}
	}

	public void resetPeriodic(Commands commands) {
		commands.lightingWantedState = Lighting.State.IDLE;
	}

	public void reset(Commands commands) {
		commands.routinesWanted.clear();
		commands.climberWantedState = Climber.State.IDLE;
		commands.climberWantsSoftLimits = true;
		commands.setDriveNeutral();
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
		commands.intakeWantedState = Intake.State.STOW;
		commands.indexerWantedHopperState = Indexer.HopperState.OPEN;
		commands.setShooterIdle();
		commands.spinnerWantedState = Spinner.State.IDLE;
		commands.lightingWantedState = Lighting.State.IDLE;
		commands.wantedCompression = true;
		commands.wantedRumble = false;
		commands.visionWanted = false;
		mOperatorXboxController.clearLastInputs();
	}
}
