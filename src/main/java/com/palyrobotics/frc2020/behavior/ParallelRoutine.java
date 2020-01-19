package com.palyrobotics.frc2020.behavior;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

/**
 * Runs all routines at the same time. Finishes when all routines are finished.
 */
public class ParallelRoutine extends MultipleRoutine {

	private final LinkedList<RoutineBase> mRunningRoutines = new LinkedList<>(mRoutines);

	public ParallelRoutine(RoutineBase... routines) {
		super(routines);
	}

	public ParallelRoutine(List<RoutineBase> routines) {
		super(routines);
	}

	@Override
	public void update(Commands commands) {
		mRunningRoutines.removeIf(runningRoutine -> runningRoutine.execute(commands));
	}

	@Override
	public boolean checkFinished() {
		return mRunningRoutines.isEmpty();
	}

	@Override
	public Set<Subsystem> getRequiredSubsystems() {
		return RoutineManager.sharedSubsystems(mRunningRoutines);
	}
}
