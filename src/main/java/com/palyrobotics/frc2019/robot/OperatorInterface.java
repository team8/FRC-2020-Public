package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeCargoRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeSensorStopRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Intake;
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
			 * Intake controls
			 */

			//Operator intake control

			//Intake wheel logic block
			if(mOperatorXboxController.getRightTrigger() > 0.0) {
				newCommands.wantedIntakingState = Intake.WheelState.FAST_EXPELLING;
				newCommands.customIntakeSpeed = true;
				newCommands.cancelCurrentRoutines = true;
			} else if(mOperatorXboxController.getLeftBumper()) {
				if (operatorButtonTwoPressable) {
					ArrayList<Routine> intakeThenUp = new ArrayList<>();
					intakeThenUp.add(new IntakeSensorStopRoutine(Intake.WheelState.INTAKING, 115.0));
					newCommands.cancelCurrentRoutines = true;
					newCommands.addWantedRoutine(new SequentialRoutine(intakeThenUp));
				}
				operatorButtonTwoPressable = false;
			} else if(mOperatorXboxController.getButtonA()) {
				newCommands.wantedIntakingState = Intake.WheelState.FAST_EXPELLING;
				newCommands.cancelCurrentRoutines = true;
			} else if(mOperatorXboxController.getLeftTrigger() > 0.0) {
				newCommands.wantedIntakingState = Intake.WheelState.INTAKING;
				newCommands.customIntakeSpeed = true;
				newCommands.cancelCurrentRoutines = true;
			} else {
				newCommands.customIntakeSpeed = false;
				operatorButtonTwoPressable = true;
				newCommands.wantedIntakingState = Intake.WheelState.IDLE;
			}

		} else {

			/**
			 * Intake controls
			 */
			//Intake wheel logic block
			if (mOperatorJoystick.getTriggerPressed()) {
				newCommands.wantedIntakingState = Intake.WheelState.FAST_EXPELLING;
				newCommands.cancelCurrentRoutines = true;
			} else if (mOperatorJoystick.getButtonPressed(2)) {
				if (operatorButtonTwoPressable) {
					ArrayList<Routine> intakeThenUp = new ArrayList<>();
					intakeThenUp.add(new IntakeCargoRoutine(3000));
					newCommands.cancelCurrentRoutines = true;
					newCommands.addWantedRoutine(new SequentialRoutine(intakeThenUp));
				}
				operatorButtonTwoPressable = false;
			} else if (mOperatorJoystick.getButtonPressed(10)) {
				newCommands.wantedIntakingState = Intake.WheelState.FAST_EXPELLING;
				newCommands.cancelCurrentRoutines = true;
			} else if (mOperatorJoystick.getButtonPressed(9)) {
				newCommands.wantedIntakingState = Intake.WheelState.INTAKING;
				newCommands.cancelCurrentRoutines = true;
			} else {
				operatorButtonTwoPressable = true;
				newCommands.wantedIntakingState = Intake.WheelState.IDLE;
			}
		}

		return newCommands;
	}
}