package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class DriveAlignYawAssistedRoutine extends RoutineBase {

	@Override
	public boolean checkFinished(RobotState state) {
		// TODO: check error with tx
		return false;
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		commands.setDriveVisionAlign();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
