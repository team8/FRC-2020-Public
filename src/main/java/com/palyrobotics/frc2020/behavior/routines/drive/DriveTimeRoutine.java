package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.control.DriveOutputs;

public class DriveTimeRoutine extends TimedRoutine {

	private final DriveOutputs mOutput;

	public DriveTimeRoutine(double durationSeconds, DriveOutputs output) {
		super(durationSeconds);
		mOutput = output;
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		commands.setDriveOutputs(mOutput);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
