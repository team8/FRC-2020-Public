package com.palyrobotics.frc2020.behavior;

import java.util.Arrays;
import java.util.List;

public abstract class MultipleRoutine extends RoutineBase {

	protected final List<RoutineBase> mRoutines;

	public MultipleRoutine(RoutineBase... routines) {
		this(Arrays.asList(routines));
	}

	public MultipleRoutine(List<RoutineBase> routines) {
		if (routines.size() <= 1) {
			throw new IllegalArgumentException("Multiple routines should have more than one routine!");
		}
		mRoutines = routines;
	}

	@Override
	public String toString() {
		StringBuilder name = new StringBuilder(super.getName()).append(":");
		for (RoutineBase routine : mRoutines) {
			name.append("\n").append("    ").append(routine).append(" ").append("[")
					.append(routine.isFinished() ? "Finished" : "Running").append("]");
		}
		return name.toString();
	}
}
