package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Subsystem;

/**
 * Created by Nihar on 1/22/17. Used for testing {@link RoutineManager} in {@link com.palyrobotics.frc2020.behavior.RoutineManagerTest}
 */
public class DrivetrainRoutine extends MockRoutine {

	private boolean isFinished;

	@Override
	public void start() {
		isFinished = false;
	}

	@Override
	public Commands update(Commands commands) {
		return null;
	}

	@Override
	public Commands cancel(Commands commands) {
		isFinished = true;
		return null;
	}

	@Override
	public boolean finished() {
		return isFinished;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		Subsystem[] required = { Drive.getInstance() };
		return required;
	}

	@Override
	public String getName() {
		return null;
	}
}