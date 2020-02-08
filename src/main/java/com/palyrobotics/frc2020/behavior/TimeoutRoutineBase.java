package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public abstract class TimeoutRoutineBase extends TimedRoutine {

	protected TimeoutRoutineBase() {
		this(0.0);
	}

	public TimeoutRoutineBase(double timeoutSeconds) {
		super(timeoutSeconds);
	}

	@Override
	public final boolean checkFinished(@ReadOnly RobotState state) {
		boolean timeoutFinished = super.checkFinished(state);
		if (timeoutFinished) {
			onTimeout();
		}
		return timeoutFinished || checkIfFinishedEarly(state);
	}

	public abstract boolean checkIfFinishedEarly(@ReadOnly RobotState state);

	public void onTimeout() {
	}
}
