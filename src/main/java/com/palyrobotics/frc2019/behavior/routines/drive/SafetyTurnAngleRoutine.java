package com.palyrobotics.frc2018.behavior.routines.drive;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.subsystems.Subsystem;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

/**
 * @author Nihar
 */
public class SafetyTurnAngleRoutine extends Routine {
	private double targetAngle;
	private Routine mRoutine;
	private RobotState robotState;

	public SafetyTurnAngleRoutine(double angle, RobotState robotState) {
		this.targetAngle = angle;
	}

	@Override
	public void start() {
		if(robotState.drivePose.heading == -0.0) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Gyro broken");
			mRoutine = new EncoderTurnAngleRoutine(targetAngle);
		} else {
			Logger.getInstance().logRobotThread(Level.INFO, "Gyro working!");
			mRoutine = new BBTurnAngleRoutine(targetAngle);
		}
		mRoutine.start();
	}

	@Override
	public Commands update(Commands commands) {
		Logger.getInstance().logRobotThread(Level.FINEST, "Angle", robotState.drivePose.heading);
		return mRoutine.update(commands);
	}

	@Override
	public Commands cancel(Commands commands) {
		return mRoutine.cancel(commands);
	}

	@Override
	public boolean finished() {
		return mRoutine.finished();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { drive };
	}

	@Override
	public String getName() {
		String name = "Safety Turn Angle with ";
		if(mRoutine instanceof BBTurnAngleRoutine) {
			name += "Gyro Turn Angle";
		} else {
			name += "Encoder Turn Angle";
		}
		return name;
	}
}
