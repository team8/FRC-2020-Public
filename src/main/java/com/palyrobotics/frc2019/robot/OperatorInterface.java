package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Arm;
import com.palyrobotics.frc2019.util.ChezyMath;
import com.palyrobotics.frc2019.util.JoystickInput;
import com.palyrobotics.frc2019.util.XboxInput;

import java.util.ArrayList;

/**
 * Used to produce Commands {@link Commands} from human input Singleton class. Should only be used in robot package.
 *
 * @author Nihar
 *
 */
public class OperatorInterface {
	private static OperatorInterface instance = new OperatorInterface();

	public static OperatorInterface getInstance() {
		return instance;
	}

	private boolean operatorButtonFourPressable = true;
	private boolean operatorButtonTwoPressable = true;

	private JoystickInput mDriveStick = Robot.getRobotState().leftStickInput;
	private JoystickInput mTurnStick = Robot.getRobotState().rightStickInput;
	private JoystickInput mClimberStick = Robot.getRobotState().climberStickInput;
	private JoystickInput mOperatorJoystick = null;
	private XboxInput mOperatorXboxController = null;

	protected OperatorInterface() {
		if(Constants.operatorXBoxController) {
			mOperatorXboxController = Robot.getRobotState().operatorXboxControllerInput;
		} else {
			mOperatorJoystick = Robot.getRobotState().operatorJoystickInput;
		}
	}

	/**
	 * Helper method to only add routines that aren't already in wantedRoutines
	 *
	 * @param commands
	 *            Current set of commands being modified
	 * @param wantedRoutine
	 *            Routine to add to the commands
	 * @return whether or not wantedRoutine was successfully added
	 */
	private boolean addWantedRoutine(Commands commands, Routine wantedRoutine) {
		for(Routine routine : commands.wantedRoutines) {
			if(routine.getClass().equals(wantedRoutine.getClass())) {
				return false;
			}
		}
		commands.wantedRoutines.add(wantedRoutine);
		return true;
	}

	/**
	 * Returns modified commands
	 *
	 * @param prevCommands
	 */
	public Commands updateCommands(Commands prevCommands) {

		Commands newCommands = prevCommands.copy();

		newCommands.cancelCurrentRoutines = false;

		/**
		 * Drivetrain controls
		 */
		if(prevCommands.wantedDriveState != Drive.DriveState.OFF_BOARD_CONTROLLER && prevCommands.wantedDriveState != Drive.DriveState.ON_BOARD_CONTROLLER) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}

		//More safety
		if(Math.abs(ChezyMath.handleDeadband(mDriveStick.getY(), Constants.kDeadband)) > 0.0
				|| Math.abs(ChezyMath.handleDeadband(mTurnStick.getX(), Constants.kDeadband)) > 0.0) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}

		if(Constants.operatorXBoxController) {
			/**
			 * Arm controls
			 */
			if(Math.abs(ChezyMath.handleDeadband(mOperatorXboxController.getRightY(), 0.05)) > 0.0) {
				newCommands.wantedArmState = Arm.ArmState.MANUAL_POSITIONING;
			} else {
				newCommands.wantedArmState = Arm.ArmState.HOLD;
			}
			// Start button
			if(mOperatorXboxController.getButtonPressed(8)) {
				newCommands.disableArmScaling = true;
			}

		} else {

			if (Math.abs(ChezyMath.handleDeadband(mOperatorJoystick.getY(), 0.02)) > 0.0) {
				newCommands.wantedArmState = Arm.ArmState.MANUAL_POSITIONING;
			} else {
				newCommands.wantedArmState = Arm.ArmState.HOLD;
			}
			if (mOperatorJoystick.getButtonPressed(11)) {
				newCommands.disableArmScaling = true;
			}

		}

		return newCommands;
	}
}