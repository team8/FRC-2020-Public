package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.Optional;
import java.util.logging.Level;

public class DriveTimeRoutine extends Routine {
	private long mEndTime;
	private SparkSignal mDrivePower;

	/**
	 * Constructs with a specified time setpoint and velocity
	 * 
	 * @param time
	 *            How long to drive (seconds)
	 * @param drivePower
	 *            LegacyDrive signal to output (left/right speeds -1 to 1)
	 */
	public DriveTimeRoutine(double time, SparkSignal drivePower) {
		//Keeps the offset prepared, when routine starts, will add System.currentTime
		mEndTime = (long) (1000 * time);
		this.mDrivePower = drivePower;
	}

	@Override
	public void start() {
		drive.resetController();
		//mEndTime already has the desired drive time
		mEndTime += System.currentTimeMillis();
	}

	//Routines just change the states of the robotsetpoints, which the behavior manager then moves the physical
	//subsystems based on.
	@Override
	public Commands update(Commands commands) {
		commands.wantedDriveState = Drive.DriveState.OPEN_LOOP;
		commands.robotSetpoints.drivePowerSetpoint = Optional.of(mDrivePower);
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		Logger.getInstance().logRobotThread(Level.FINE, "Cancelling");
		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
		drive.resetController();
		drive.setNeutral();
		return commands;
	}

	@Override
	public boolean finished() {
		//Finish after the time is up
		return (System.currentTimeMillis() >= mEndTime);
	}

	@Override
	public String getName() {
		return "DriveTimeRoutine";
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] { drive };
	}
}