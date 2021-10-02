package com.palyrobotics.frc2020.robot;

import static com.palyrobotics.frc2020.util.Util.handleDeadBand;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;
import static com.palyrobotics.frc2020.vision.Limelight.kTwoTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerPositionControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.SpinnerRotationControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedRoutine;
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
		updateSuperstructureCommands(commands, state);
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
		if (mLimelight.isTargetFound()) {
			commands.lightingWantedState = Lighting.State.TARGET_FOUND;
		}
		if (mTurnStick.getRawButton(3) || mTurnStick.getRawButton(4) && mLimelight.isAligned()) {
			commands.lightingWantedState = Lighting.State.ROBOT_ALIGNED;
		}
		if (commands.climberWantedState == Climber.State.LOCKED) {
			commands.lightingWantedState = Lighting.State.CLIMB_DONE;
		}
	}

	private void updateSuperstructureCommands(Commands commands, RobotState state) {
		if (mOperatorXboxController.getDPadDownReleased()) {
			commands.setIntakeRunning(0);
		} else if (mOperatorXboxController.getDPadDown()) {
			if (!state.intakeStalled) {
				commands.setIntakeRunning(mIntakeConfig.rollerPo);
			} else {
				commands.setIntakeRunning(-mIntakeConfig.rollerPo);
			}
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.FORWARD;
		} else if (mOperatorXboxController.getDPadUp()) {
			commands.setIntakeStowed();
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.IDLE;
		}
		if (mOperatorXboxController.getRightTriggerPressed()) {
			commands.addWantedRoutine(new IndexerFeedRoutine());
		} else if (mOperatorXboxController.getLeftTrigger()) {
			commands.indexerColumnWantedState = Indexer.ColumnState.REVERSE_FEED;
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.REVERSE;
		} else if ((state.indexerPos1Blocked && !state.indexerPos4Blocked) || mOperatorXboxController.getXButton()) {
			commands.indexerColumnWantedState = Indexer.ColumnState.INDEX;
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.FORWARD;
		} else if (mOperatorXboxController.getBButton()) {
			commands.indexerColumnWantedState = Indexer.ColumnState.UN_INDEX;
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.REVERSE;
		} else {
			commands.indexerColumnWantedState = Indexer.ColumnState.IDLE;
		}
		if (mOperatorXboxController.getRightBumper()) {
			commands.setShooterCustomFlywheelVelocity(1500, Shooter.HoodState.MIDDLE);
		} else if (mOperatorXboxController.getLeftBumper()) {
			commands.setIntakeStowed();
			commands.indexerColumnWantedState = Indexer.ColumnState.IDLE;
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.IDLE;
			commands.setShooterIdle();
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
		commands.wantedCompression = true;
		commands.visionWanted = false;
		commands.setIntakeStowed();
		commands.setShooterIdle();
		commands.spinnerWantedState = Spinner.State.IDLE;
		commands.indexerColumnWantedState = Indexer.ColumnState.IDLE;
		commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.IDLE;
		mOperatorXboxController.clearLastInputs();
	}
}
