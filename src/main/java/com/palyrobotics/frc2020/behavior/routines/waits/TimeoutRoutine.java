package com.palyrobotics.frc2020.behavior.routines.waits;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;

public abstract class TimeoutRoutine extends TimedRoutine {

	protected TimeoutRoutine() {
		this(0.0);
	}

	public TimeoutRoutine(double timeout) {
		super(timeout);
	}

	@Override
	public final boolean checkFinished() {
		return super.checkFinished() || checkIfFinishedEarly();
	}

	public abstract boolean checkIfFinishedEarly();
}
