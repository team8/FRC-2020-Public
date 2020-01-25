package com.palyrobotics.frc2020.behavior.routines.waits;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public abstract class TimeoutRoutineBase extends TimedRoutine {

	protected TimeoutRoutineBase() {
		this(0.0);
	}

	public TimeoutRoutineBase(double timeout) {
		super(timeout);
	}

	@Override
	public final boolean checkFinished(@ReadOnly RobotState state) {
		return super.checkFinished(state) || checkIfFinishedEarly(state);
	}

	public abstract boolean checkIfFinishedEarly(@ReadOnly RobotState state);
}
