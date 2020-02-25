package com.palyrobotics.frc2020.behavior;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

/**
 * Runs all routines at the same time. Finishes when all routines are finished.
 */
public class ParallelRoutine extends MultipleRoutineBase {

	protected final LinkedList<RoutineBase> mRunningRoutines = new LinkedList<>(mRoutines);

	public ParallelRoutine(RoutineBase... routines) {
		super(routines);
	}

	public ParallelRoutine(List<RoutineBase> routines) {
		super(routines);
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		mRunningRoutines.removeIf(runningRoutine -> {
			boolean isFinished = runningRoutine.execute(commands, state);
			if (isFinished) {
				Log.debug(getName(), String.format("Dropping routine: %s", runningRoutine.getName()));
			}
			return isFinished;
		});
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		for (RoutineBase runningRoutine : mRunningRoutines) {
			runningRoutine.stop(commands, state);
		}
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mRunningRoutines.isEmpty();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return RoutineManager.sharedSubsystems(mRunningRoutines);
	}
}
