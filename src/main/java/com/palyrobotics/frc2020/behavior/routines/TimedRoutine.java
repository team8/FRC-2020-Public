package com.palyrobotics.frc2020.behavior.routines;

import java.util.HashSet;
import java.util.Set;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.subsystems.Subsystem;

import edu.wpi.first.wpilibj.Timer;

public class TimedRoutine extends Routine {

	private final Timer mTimer = new Timer();
	private double mTimeout;

	/**
	 * Routine that waits the specified amount of time. Does not require any
	 * subsystems.
	 */
	public TimedRoutine(double durationSeconds) {
		mTimeout = durationSeconds;
	}

	@Override
	public void start() {
		mTimer.start();
	}

	@Override
	public boolean checkFinished() {
		return mTimer.get() > mTimeout;
	}

	@Override
	public Set<Subsystem> getRequiredSubsystems() {
		return new HashSet<>();
	}
}
