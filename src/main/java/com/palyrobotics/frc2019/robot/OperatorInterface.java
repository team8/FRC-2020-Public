package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.shovel.ShovelDownRoutine;
import com.palyrobotics.frc2019.behavior.routines.shovel.ShovelUpRoutine;
import com.palyrobotics.frc2019.behavior.routines.shovel.ShovelWheelRoutine;
import com.palyrobotics.frc2019.behavior.routines.shovel.WaitForHatchIntakeCurrentSpike;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.config.Constants.FingerConstants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeUpRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.ChezyMath;
import com.palyrobotics.frc2019.util.JoystickInput;
import com.palyrobotics.frc2019.util.XboxInput;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.vision.Limelight;

import java.util.logging.Level;

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

	private JoystickInput mDriveStick = Robot.getRobotState().leftStickInput;
	private JoystickInput mTurnStick = Robot.getRobotState().rightStickInput;
	private JoystickInput mClimbStick = Robot.getRobotState().backupStickInput;
	private XboxInput mOperatorXboxController;

	public static boolean demandIntakeUp = true; // force intake to come up
	public static boolean intakeRunning = false; // force intake to come up
	public static double intakeStartTime = 0; // force intake to come up
	public static double lastCancelTime = 0;


	protected OperatorInterface() {
		mOperatorXboxController = Robot.getRobotState().operatorXboxControllerInput;
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
		if(Math.abs(ChezyMath.handleDeadband(mDriveStick.getY(), DrivetrainConstants.kDeadband)) > 0.0
				|| Math.abs(ChezyMath.handleDeadband(mTurnStick.getX(), DrivetrainConstants.kDeadband)) > 0.0) {
			newCommands.wantedDriveState = Drive.DriveState.CHEZY;
		}

		if(mTurnStick.getButtonPressed(3)) {
			newCommands.wantedDriveState = Drive.DriveState.VISION_ASSIST;
		}

		/**
		 * Hatch Ground Intake/Shovel Control
		 */
//		if(mOperatorXboxController.getButtonX()) {
//			if(prevCommands.wantedShovelUpDownState == Shovel.UpDownState.UP) {
//				newCommands.wantedShovelUpDownState = Shovel.UpDownState.DOWN;
//				newCommands.cancelCurrentRoutines = true;
//			} else if (prevCommands.wantedShovelUpDownState == Shovel.UpDownState.DOWN) {
//				newCommands.wantedShovelUpDownState = Shovel.UpDownState.UP;
//				newCommands.cancelCurrentRoutines = true;
//			}
//		}

		if (mOperatorXboxController.getButtonX() && newCommands.wantedShovelUpDownState == Shovel.UpDownState.UP  && (lastCancelTime + 200) < System.currentTimeMillis()) {
			intakeStartTime = System.currentTimeMillis();
			newCommands.addWantedRoutine(new SequentialRoutine(new ShovelDownRoutine(), new WaitForHatchIntakeCurrentSpike(Shovel.WheelState.INTAKING),
					new ShovelUpRoutine()));
		} else if (mOperatorXboxController.getButtonX() && (System.currentTimeMillis() - 350 > OperatorInterface.intakeStartTime) && newCommands.wantedShovelUpDownState == Shovel.UpDownState.DOWN) {
		    intakeStartTime = System.currentTimeMillis();
		    newCommands.wantedShovelUpDownState = Shovel.UpDownState.UP;
		    lastCancelTime = System.currentTimeMillis();
        }

//		newCommands.wantedElevatorState = Elevator.ElevatorState.MANUAL_POSITIONING;

		/**
		 * Elevator Control
		 */
		if(mOperatorXboxController.getButtonA()) {
			Routine elevatorLevel1 = new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorCargoHeight1Inches, 2);
			newCommands.cancelCurrentRoutines = false;
			newCommands.addWantedRoutine(elevatorLevel1);
			newCommands.addWantedRoutine(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 0));
		} else if(mOperatorXboxController.getButtonB()) {
			Routine elevatorLevel2 = new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorCargoHeight2Inches, 2);
			newCommands.cancelCurrentRoutines = false;
			newCommands.addWantedRoutine(elevatorLevel2);
			newCommands.addWantedRoutine(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 0));
		} else if(mOperatorXboxController.getButtonY()) {
			Routine elevatorLevel3 = new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorCargoHeight3Inches, 2);
			newCommands.cancelCurrentRoutines = false;
			newCommands.addWantedRoutine(elevatorLevel3);
			newCommands.addWantedRoutine(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 0));
		}

		/**
		 * Cargo Intake Control
		 */
		if(mOperatorXboxController.getdPadDown() && prevCommands.wantedIntakeState == Intake.IntakeMacroState.DROPPING) {
			newCommands.cancelCurrentRoutines = false;
			newCommands.addWantedRoutine(new IntakeBeginCycleRoutine());
		} else if(mOperatorXboxController.getdPadUp() && prevCommands.wantedIntakeState == Intake.IntakeMacroState.GROUND_INTAKING) {
			newCommands.cancelCurrentRoutines = false;
			newCommands.addWantedRoutine(new IntakeUpRoutine());
		}

		if (mClimbStick.getButtonPressed(4)) {
			newCommands.wantedGearboxState = Elevator.GearboxState.CLIMBER;
		}

		if (mClimbStick.getButtonPressed(5)) {
			newCommands.wantedGearboxState = Elevator.GearboxState.ELEVATOR;
		}

		/**
		 * Climber Control
		 */
		if(mClimbStick.getButtonPressed(6)) {
			newCommands.wantedClimberState = Elevator.ClimberState.ON_MANUAL;
		}
		else {
			newCommands.wantedClimberState = Elevator.ClimberState.IDLE;
		}

		/**
		 * Pusher Control
		 */
		if(mOperatorXboxController.getLeftBumper()) {
			newCommands.wantedPusherInOutState = Pusher.PusherState.IN;
		} else if(mOperatorXboxController.getRightBumper()) {
			newCommands.wantedPusherInOutState = Pusher.PusherState.OUT;
		}

		/**
		 * Pneumatic Hatch Pusher Control
		 */
		if(mOperatorXboxController.getRightTriggerPressed()) {
			Routine hatchCycle = new FingersCycleRoutine(FingerConstants.kFingersCycleTime);
			newCommands.cancelCurrentRoutines = false;
			newCommands.addWantedRoutine(hatchCycle);
		}

		/**
		 * Shooter Spin Up Control
		 */
		if(mOperatorXboxController.getLeftTriggerPressed()) {
			newCommands.addWantedRoutine(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 5));
		}

		/**
		 * Cancel all Routines
		 */
		if(mDriveStick.getTriggerPressed()) {
			newCommands.cancelCurrentRoutines = true;
		}
		return newCommands;
	}
}