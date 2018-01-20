package com.palyrobotics.frc2018.behavior;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Subsystem;

/**
 * Created by Nihar on 1/22/17.
 * Used for testing {@link RoutineManager} in {@link RoutineManagerTest}
 */
public class MockRoutine extends Routine {

	private boolean isFinished;

	@Override
	public void start() {
		isFinished = false;
	}

	@Override
	public Commands update(Commands commands) {
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		isFinished = true;
		return commands;
	}

	@Override
	public boolean finished() {
		return isFinished;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		// Intentionally empty so as to not conflict
		return new Subsystem[3];
	}

	@Override
	public String getName() {
		return "SampleRoutine";
	}
}
