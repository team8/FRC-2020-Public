package com.palyrobotics.frc2020.behavior.routines;

import java.util.List;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public class ParallelRaceRoutine extends ParallelRoutine {

	public ParallelRaceRoutine(RoutineBase... routines) {
		super(routines);
	}

	public ParallelRaceRoutine(List<RoutineBase> routines) {
		super(routines);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		if (super.checkFinished(state)) return true;
		for (RoutineBase runningRoutine : mRunningRoutines) {
			if (runningRoutine.isFinished()) return true;
		}
		return false;
	}
}
