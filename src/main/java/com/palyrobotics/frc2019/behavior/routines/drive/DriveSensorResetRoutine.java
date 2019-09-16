package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;

/**
 * Created by EricLiu on 4/13/17.
 */
public class DriveSensorResetRoutine extends Routine {

	private double mTimeout;
	private long mStartTime;

	/**
	 *
	 * @param timeout seconds
	 */
	public DriveSensorResetRoutine(double timeout) {
		mTimeout = timeout;
	}

	@Override
	public void start() {
		this.mStartTime = System.currentTimeMillis();
		HardwareAdapter.getInstance().getDrivetrain().resetSensors();
		robotState.reset(0, new RigidTransform2d());
		robotState.drivePose.heading = 0.0;
        robotState.drivePose.leftEnc = 0.0;
        robotState.drivePose.rightEnc = 0.0;
        robotState.drivePose.lastHeading = 0.0;
		robotState.drivePose.lastLeftEnc = 0.0;
		robotState.drivePose.lastRightEnc = 0.0;
	}

	@Override
	public Commands update(Commands commands) {
		Commands output = commands.copy();
		return output;
	}

	@Override
	public Commands cancel(Commands commands) {
		Commands output = commands.copy();
		return output;
	}

	@Override
	public boolean finished() {
		if(System.currentTimeMillis() - mStartTime > mTimeout * 1000) {
//			Logger.getInstance().logRobotThread(Level.WARNING, "Drive sensor reset routine timed out!");
			return true;
		} else if(Math.abs(drive.getPose().leftEnc) <= DrivetrainConstants.kAcceptableEncoderZeroError
				&& Math.abs(drive.getPose().rightEnc) <= DrivetrainConstants.kAcceptableEncoderZeroError
				&& Math.abs(drive.getPose().heading) <= DrivetrainConstants.kAcceptableGyroZeroError) {
			return true;
		}
		return false;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { drive };
	}

	@Override
	public String getName() {
		return "DriveSensorResetRoutine";
	}
}
