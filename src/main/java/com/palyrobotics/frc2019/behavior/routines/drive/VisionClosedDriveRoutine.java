package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;

public class VisionClosedDriveRoutine extends Routine {

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { drive };
	}

	private double mAngle;

	private State mState = State.START;
	private double startTime;

	private enum State {
		START, DRIVING, TIMED_OUT, DONE
	}

	public VisionClosedDriveRoutine() {}

	@Override
	public void start() {
		drive.setNeutral();
		mState = State.START;
		startTime = System.currentTimeMillis();
		Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.VISION);
		Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON); // Limelight LED on
	}

	@Override
	public Commands update(Commands commands) {
		if(mState != State.TIMED_OUT && (System.currentTimeMillis() - startTime > 5000)) {
//			Logger.getInstance().logRobotThread(Level.WARNING, "Timed Out!");
			mState = State.TIMED_OUT;
		}
		switch(mState) {
			case START:
				drive.setVisionClosedDriveController();
				commands.wantedDriveState = Drive.DriveState.CLOSED_VISION_ASSIST;
				mState = State.DRIVING;
				break;
			case DRIVING:
				if(drive.controllerOnTarget()) {
					mState = State.DONE;
				}
				break;
			case TIMED_OUT:
				drive.setNeutral();
				break;
			case DONE:
				drive.resetController();
				break;
		}

		return commands;
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
	public String getName() {
		return "VisionClosedDrive";
	}

}
