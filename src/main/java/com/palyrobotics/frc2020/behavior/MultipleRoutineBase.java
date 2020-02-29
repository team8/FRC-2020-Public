package com.palyrobotics.frc2020.behavior;

import java.util.List;

public abstract class MultipleRoutineBase extends RoutineBase {

	protected final List<RoutineBase> mRoutines;

	public MultipleRoutineBase(RoutineBase... routines) {
		this(List.of(routines));
	}

	public MultipleRoutineBase(List<RoutineBase> routines) {
		mRoutines = routines;
	}

	public List<RoutineBase> getRoutines() {
		return mRoutines;
	}

	@Override
	public String toString() {
		var status = new StringBuilder(super.getName()).append(":");
		for (RoutineBase routine : mRoutines) {
			status.append("\n").append("    ").append(routine)
					.append(" ").append("[").append(routine.getStatus()).append("]");
		}
		return status.toString();
	}
}
