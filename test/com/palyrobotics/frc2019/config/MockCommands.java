package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.util.DriveSignal;

import java.util.Optional;

public class MockCommands extends Commands {
	
	private static MockCommands instance = new MockCommands();

	public static MockCommands getInstance() {
		return instance;
	}
	
	private MockCommands() {
		super();
	}
	
	@Override
	/**
	 * @return a copy of these commands
	 */
	public MockCommands copy() {
		MockCommands copy = new MockCommands();
		copy.wantedDriveState = this.wantedDriveState;
		copy.wantedClimbMovement = this.wantedClimbMovement;
		copy.wantedLockState = this.wantedLockState;
		copy.wantedElevatorState = this.wantedElevatorState;
		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
		copy.wantedIntakingState = this.wantedIntakingState;
		copy.wantedIntakeUpDownState = this.wantedIntakeUpDownState;
		copy.wantedIntakeOpenCloseState = this.wantedIntakeOpenCloseState;

//		copy.cancelCurrentRoutines = this.cancelCurrentRoutines;

		for(Routine r : this.wantedRoutines) {
			copy.wantedRoutines.add(r);
		}

		//Copy robot set points
		copy.robotSetpoints = new MockSetpoints();
		//Copy optionals that are present
		robotSetpoints.drivePowerSetpoint.ifPresent((DriveSignal signal) -> copy.robotSetpoints.drivePowerSetpoint = Optional.of(signal));
		robotSetpoints.elevatorPositionSetpoint
				.ifPresent((Double elevatorPositionSetpoint) -> copy.robotSetpoints.elevatorPositionSetpoint = Optional.of(elevatorPositionSetpoint));
		return copy;
	}
	
	public static class MockSetpoints extends Setpoints {
		public void setDrivePowerSetpoint(DriveSignal driveSignal) {
			this.drivePowerSetpoint = Optional.of(driveSignal);
		}
	}
	public MockSetpoints mockSetpoints = new MockSetpoints();
	
	public boolean cancelCurrentRoutines = false;
}