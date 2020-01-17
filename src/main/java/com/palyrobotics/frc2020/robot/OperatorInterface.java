package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.palyrobotics.frc2020.auto.modes.EnemyTrenchRunTwoShootFive;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

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

	private static OperatorInterface sInstance = new OperatorInterface();
	private final Limelight mLimelight = Limelight.getInstance();
	private final Joystick mDriveStick = HardwareAdapter.Joysticks.getInstance().driveStick,
			mTurnStick = HardwareAdapter.Joysticks.getInstance().turnStick;
	private final XboxController mOperatorXboxController = HardwareAdapter.Joysticks
			.getInstance().operatorXboxController;
	private final List<Pose2d> kTestWaypoints = List.of(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)),
			new Pose2d(Units.inchesToMeters(200.0), 0.0, Rotation2d.fromDegrees(0.0)));

	public static OperatorInterface getInstance() {
		return sInstance;
	}

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
	* Returns modified commands
	*
	* @param commands Last commands
	*/
	Commands updateCommands(Commands commands) {

		commands.shouldClearCurrentRoutines = false;

		updateDriveCommands(commands);
		updateSpinnerCommands(commands);
		updateIntakeCommands(commands);

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		mOperatorXboxController.updateLastInputs();

		return commands;
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
		if (mOperatorXboxController.getDPadUp()) {
			commands.addWantedRoutine(new EnemyTrenchRunTwoShootFive().getRoutine());
		} else if (mOperatorXboxController.getDPadDown()) {
			commands.addWantedRoutine(new DrivePathRoutine(true, kTestWaypoints));
		}
	}

	private void updateSpinnerCommands(Commands commands) {
		//TODO Find different button to use
		if (mOperatorXboxController.getDPadDownPressed()) {
			commands.spinnerWantedState = Spinner.SpinnerState.TO_COLOR;
		}
		if (mOperatorXboxController.getDPadUpPressed()) {
			commands.spinnerWantedState = Spinner.SpinnerState.SPIN;
		}
	}

	private void updateIntakeCommands(Commands commands) {
		if (mOperatorXboxController.getRightBumperPressed()) {
			commands.intakeWantedState = Intake.IntakeState.INTAKE;
		} else {
			commands.intakeWantedState = Intake.IntakeState.IDLE;
		}
	}

	private void updateIndexerCommands(Commands commands) {
		if (mTurnStick.getRawButtonPressed(3)) {
			commands.indexerWantedState = Indexer.IndexerState.INDEX;
		} else {
			commands.indexerWantedState = Indexer.IndexerState.IDLE;
		}
	}

	private void setVision(boolean on) {
		mLimelight.setCamMode(on ? LimelightControlMode.CamMode.VISION : LimelightControlMode.CamMode.DRIVER);
		mLimelight.setLEDMode(on ? LimelightControlMode.LedMode.FORCE_ON : LimelightControlMode.LedMode.FORCE_OFF);
	}
}
