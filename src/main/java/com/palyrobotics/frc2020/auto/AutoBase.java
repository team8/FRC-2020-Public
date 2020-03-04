package com.palyrobotics.frc2020.auto;

import java.util.List;

import com.palyrobotics.frc2020.behavior.RoutineBase;

import edu.wpi.first.wpilibj.trajectory.Trajectory;

public abstract class AutoBase {

	public abstract RoutineBase getRoutine();

	public List<Trajectory.State> getFullTrajectoryStates() {
		// TODO: implement
		RoutineBase routine = getRoutine();
		return null;
	}

}
