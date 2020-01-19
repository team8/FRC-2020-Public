package com.palyrobotics.frc2020.behavior.routines.waits;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;

public abstract class TimeoutRoutineBase extends TimedRoutine {

	protected TimeoutRoutineBase() {
		this(0.0);
	}

	public TimeoutRoutineBase(double timeout) {
		super(timeout);
	}

	@Override
	public final boolean checkFinished() {
		return super.checkFinished() || checkIfFinishedEarly();
	}

	public abstract boolean checkIfFinishedEarly();
}
