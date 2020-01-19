package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class DriveTurnRoutine extends RoutineBase {

	@Override
	protected void update(Commands commands) {
		commands.setDriveTurn(0.0);
	}

	@Override
	public Set<Subsystem> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
