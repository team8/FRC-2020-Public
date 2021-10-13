package com.palyrobotics.frc2020.auto;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.behavior.RoutineBase;

import edu.wpi.first.wpilibj.trajectory.Trajectory;

public abstract class AutoBase {

	public abstract RoutineBase getRoutine() throws JsonProcessingException;

	public List<Trajectory.State> getFullTrajectoryStates() throws JsonProcessingException {
		// TODO: implement
		RoutineBase routine = getRoutine();
		return null;
	}

}
