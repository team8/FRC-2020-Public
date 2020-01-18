package com.palyrobotics.frc2020.behavior.routines.climber;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Climber;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class ClimberCustomPositioningRoutine extends RoutineBase {

	private double mPosition;

	public ClimberCustomPositioningRoutine(double position) {
		mPosition = position;
	}

	@Override
	public void update(Commands commands) {
		commands.climberWantedState = Climber.ClimberState.CUSTOM_POSITION;
		commands.setClimberWantedPosition(mPosition);
	}

	@Override
	public Set<Subsystem> getRequiredSubsystems() {
		return Set.of(mClimber);
	}
}
