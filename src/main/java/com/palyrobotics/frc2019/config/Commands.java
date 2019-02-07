package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.SparkSignal;
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

	public Shooter.ShooterState wantedShooterState = Shooter.ShooterState.IDLE;

	public Pusher.PusherState wantedPusherInOutState = Pusher.PusherState.IN;

	public Shovel.WheelState wantedShovelWheelState = Shovel.WheelState.IDLE;
	public Shovel.UpDownState wantedShovelUpDownState = Shovel.UpDownState.UP;

	public Fingers.FingersState wantedFingersOpenCloseState = Fingers.FingersState.CLOSE;
	public Fingers.PushingState wantedFingersExpelState = Fingers.PushingState.CLOSED;

	public Intake.IntakeMacroState wantedIntakeState = Intake.IntakeMacroState.STOWED;

	public boolean disableIntakeScaling = true;

	public boolean customShooterSpeed = false;
	public boolean customIntakeSpeed = false;

	public Elevator.ElevatorState wantedElevatorState = Elevator.ElevatorState.CALIBRATING;
	public Elevator.ClimberState wantedClimberState = Elevator.ClimberState.INACTIVE;
	public Elevator.GearboxState wantedGearboxState = Elevator.GearboxState.ELEVATOR;

	public boolean customShovelSpeed = false;
	public boolean autoPlacerOutput = false;

	public boolean intakeHFX = false;
	public boolean hasCargo = false;
	public boolean hasPusherCargo = false;

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
		public Optional<SparkSignal> drivePowerSetpoint = Optional.empty();
		public Optional<Double> elevatorPositionSetpoint = Optional.empty();
		public Optional<Double> climberPositionSetpoint = Optional.empty();
		public Optional<Double> intakePositionSetpoint = Optional.empty();
		/**
		 * Resets all the setpoints
		 */
		public void reset() {
			drivePowerSetpoint = Optional.empty();
			elevatorPositionSetpoint = Optional.empty();
			climberPositionSetpoint = Optional.empty();
			intakePositionSetpoint = Optional.empty();
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
		copy.wantedShooterState = this.wantedShooterState;
		copy.wantedElevatorState = this.wantedElevatorState;
		copy.wantedClimberState = this.wantedClimberState;
		copy.wantedGearboxState = this.wantedGearboxState;
		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.wantedPusherInOutState = this.wantedPusherInOutState;
		copy.wantedShovelWheelState = this.wantedShovelWheelState;
		copy.wantedShovelUpDownState = this.wantedShovelUpDownState;
		copy.wantedFingersOpenCloseState = this.wantedFingersOpenCloseState;
		copy.wantedFingersExpelState = this.wantedFingersExpelState;
		copy.wantedShovelWheelState = this.wantedShovelWheelState;
		copy.wantedShovelUpDownState = this.wantedShovelUpDownState;
		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.customShooterSpeed = this.customShooterSpeed;
		copy.customIntakeSpeed = this.customIntakeSpeed;
		copy.customShovelSpeed = this.customShovelSpeed;
		copy.autoPlacerOutput = this.autoPlacerOutput;
		copy.wantedIntakeState = this.wantedIntakeState;
		copy.disableIntakeScaling = this.disableIntakeScaling;
		copy.hasPusherCargo = this.hasPusherCargo;

		for(Routine r : this.wantedRoutines) {
			copy.wantedRoutines.add(r);
		}

		//Copy robot setpoints
		copy.robotSetpoints = new Setpoints();
		//Copy optionals that are present
		robotSetpoints.drivePowerSetpoint.ifPresent((SparkSignal signal) -> copy.robotSetpoints.drivePowerSetpoint = Optional.of(signal));
		robotSetpoints.elevatorPositionSetpoint.ifPresent((Double elevatorPositionSetpoint) -> copy.robotSetpoints.elevatorPositionSetpoint = Optional.of(elevatorPositionSetpoint));
        robotSetpoints.climberPositionSetpoint.ifPresent((Double climberPositionSetpoint) -> copy.robotSetpoints.climberPositionSetpoint = Optional.of(climberPositionSetpoint));
		robotSetpoints.intakePositionSetpoint
				.ifPresent((Double elevatorPositionSetpoint) -> copy.robotSetpoints.intakePositionSetpoint = Optional.of(elevatorPositionSetpoint));
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