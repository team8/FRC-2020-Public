package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class DriveStraightRoutine extends Routine {

	private double distance;

	public DriveStraightRoutine(double distance) {
		this.distance = distance;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { drive };
	}

	/*
	 * START = Set new drive setpoint DRIVING = Waiting to reach drive setpoint DONE = reached target or not operating
	 */
	private enum DriveStraightRoutineState {
		START, DRIVING, DONE
	}

	DriveStraightRoutineState state = DriveStraightRoutineState.START;

	@Override
	public void start() {
		drive.setNeutral();
		state = DriveStraightRoutineState.START;
	}

	@Override
	public Commands update(Commands commands) {
		Commands output = commands.copy();
		switch(state) {
			case START:
				drive.setDriveStraight(distance);
				output.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
				state = DriveStraightRoutineState.DRIVING;
				break;
			case DRIVING:
				output.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
				if(drive.controllerOnTarget() && drive.hasController()) {
					state = DriveStraightRoutineState.DONE;
				}
				break;
			case DONE:
				drive.resetController();
				break;
			default:
				break;
		}
		return output;
	}

	@Override
	public Commands cancel(Commands commands) {
//		Logger.getInstance().logRobotThread(Level.FINE, "Cancelling DriveStraightRoutine");
		state = DriveStraightRoutineState.DONE;
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
		drive.resetController();
		return commands;
	}

	@Override
	public boolean finished() {
		return state == DriveStraightRoutineState.DONE;
	}

	@Override
	public String getName() {
		return "DriveStraightRoutine";
	}

}
