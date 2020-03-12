package com.palyrobotics.frc2020.behavior;

import java.util.List;

import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

/**
 * Runs all the routines at the same time, but finishes all when one finishes first. As such, it is
 * okay to have routines with infinite timeouts as long as there is one with a defined finish time.
 */
public class ParallelRaceRoutine extends ParallelRoutine {

	public ParallelRaceRoutine(RoutineBase... routines) {
		super(routines);
	}

	public ParallelRaceRoutine(List<RoutineBase> routines) {
		super(routines);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mRoutines.stream().anyMatch(RoutineBase::isFinished);
	}
}
