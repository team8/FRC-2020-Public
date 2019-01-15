package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Arm;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Commands represent the desired setpoints and subsystem states for the robot. <br />
 * Store Requests (enum) for each subsystem and setpoints {@link Setpoints} <br />
 * Variables are public and have default values to prevent NullPointerExceptions
 * 
 * @author Nihar
 *
 */
public class Commands {

	private static Commands instance = new Commands();

	public static Commands getInstance() {
		return instance;
	}

	protected Commands() {
	}

	public ArrayList<Routine> wantedRoutines = new ArrayList<Routine>();

	//Store WantedStates for each subsystem state machine
	public Drive.DriveState wantedDriveState = Drive.DriveState.NEUTRAL;
	public Arm.ArmState wantedArmState = Arm.ArmState.IDLE;
	public Shooter.ShooterState wantedShooterState = Shooter.ShooterState.IDLE;
	public Intake.WheelState wantedIntakingState = Intake.WheelState.IDLE;
	public Intake.OpenCloseState wantedIntakeOpenCloseState = Intake.OpenCloseState.CLOSED;
	public Pusher.PusherState wantedPusherInOutState = Pusher.PusherState.IN;
	public boolean disableArmScaling = true;
	public boolean customShooterSpeed = false;
	public boolean customIntakeSpeed = false;

	public void addWantedRoutine(Routine wantedRoutine) {
		for(Routine routine : wantedRoutines) {
			if(routine.getClass().equals(wantedRoutine.getClass())) {
				Logger.getInstance().logRobotThread(Level.WARNING, "tried to add duplicate routine", routine.getName());
				return;
			}
		}
		wantedRoutines.add(wantedRoutine);
	}

	public static void reset() {
		instance = new Commands();
	}

	/**
	 * Stores numeric setpoints
	 * 
	 * @author Nihar
	 */
	public static class Setpoints {
		public Optional<DriveSignal> drivePowerSetpoint = Optional.empty();
		public Optional<Double> armPositionSetpoint = Optional.empty();

		/**
		 * Resets all the setpoints
		 */
		public void reset() {
			drivePowerSetpoint = Optional.empty();
			armPositionSetpoint = Optional.empty();
		}
	}

	//All robot setpoints
	public Setpoints robotSetpoints = new Setpoints();

	//Allows you to cancel all running routines
	public boolean cancelCurrentRoutines = false;

	/**
	 * @return a copy of these commands
	 */
	public Commands copy() {
		Commands copy = new Commands();
		copy.wantedDriveState = this.wantedDriveState;
		copy.wantedArmState = this.wantedArmState;
		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.wantedIntakingState = this.wantedIntakingState;
		copy.wantedIntakeOpenCloseState = this.wantedIntakeOpenCloseState;
		copy.wantedPusherInOutState = this.wantedPusherInOutState;
		copy.disableArmScaling = this.disableArmScaling;
		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.customShooterSpeed = this.customShooterSpeed;
		copy.customIntakeSpeed = this.customIntakeSpeed;

		for(Routine r : this.wantedRoutines) {
			copy.wantedRoutines.add(r);
		}

		//Copy robot setpoints
		copy.robotSetpoints = new Setpoints();
		//Copy optionals that are present
		robotSetpoints.drivePowerSetpoint.ifPresent((DriveSignal signal) -> copy.robotSetpoints.drivePowerSetpoint = Optional.of(signal));
		robotSetpoints.armPositionSetpoint
				.ifPresent((Double elevatorPositionSetpoint) -> copy.robotSetpoints.armPositionSetpoint = Optional.of(elevatorPositionSetpoint));
		return copy;
	}

	@Override
	public String toString() {
		String log = "";
		String wantedRoutineName = "";
		for(Routine r : this.wantedRoutines) {
			wantedRoutineName += r.getName() + " ";
		}
		log += "Wanted Routines: " + wantedRoutineName + "\n";

		return log;
	}
}