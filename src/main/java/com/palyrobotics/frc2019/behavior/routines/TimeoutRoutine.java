package com.palyrobotics.frc2018.behavior.routines;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Subsystem;

public class TimeoutRoutine extends Routine {
	double timeout;
	double timeStart;

	/**
	 * Routine that waits the specified amount of time <br />
	 * Does not require any subsystems
	 * 
	 * @param waitTime
	 *            time to wait in seconds
	 */
	public TimeoutRoutine(double waitTime) {
		this.timeout = waitTime;
	}

	@Override
	public boolean finished() {
		return System.currentTimeMillis() >= timeStart + 1000 * timeout;
	}

	@Override
	public Commands update(Commands commands) {
		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		return commands;
	}

	@Override
	public void start() {
		timeStart = System.currentTimeMillis();
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return new Subsystem[] {};
	}

	@Override
	public String getName() {
		return "TimeoutRoutine";
	}

}
