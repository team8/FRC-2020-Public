package com.palyrobotics.frc2018.behavior.routines.drive;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.subsystems.Subsystem;

public class EncoderTurnAngleRoutine extends Routine {

	private double angle;

	private enum State {
		START, TURNING, DONE
	}

	private State mState = State.START;

	public EncoderTurnAngleRoutine(double angle) {
		this.angle = angle;
	}

	@Override
	public void start() {
		drive.setNeutral();
		mState = State.START;
	}

	@Override
	public Commands update(Commands commands) {
		Commands output = commands.copy();
		switch(mState) {
			case START:
				drive.setTurnAngleEncoderSetpoint(angle);
				output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
				mState = State.TURNING;
				break;
			case TURNING:
				output.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
				if(drive.controllerOnTarget() && drive.hasController()) {
					mState = State.DONE;
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
		mState = State.DONE;
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
		drive.setNeutral();
		return commands;
	}

	@Override
	public boolean finished() {
		return mState == State.DONE;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { drive };
	}

	@Override
	public String getName() {
		return "EncoderTurnAngleRoutine";
	}

}
