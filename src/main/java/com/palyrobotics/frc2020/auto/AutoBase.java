package com.palyrobotics.frc2020.auto;

import java.util.List;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeStowRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.IndexerFeedAllRoutine;

import edu.wpi.first.wpilibj.trajectory.Trajectory;

public abstract class AutoBase {

	public abstract RoutineBase getRoutine();

	public List<Trajectory.State> getFullTrajectoryStates() {
		// TODO: implement
		RoutineBase routine = getRoutine();
		return null;
	}

	/* Routines that are just combinations of other routines */

	ParallelRoutine getBallsRoutine = new ParallelRoutine(
			new TimedRoutine(1),
			new IntakeBallRoutine());

	ParallelRoutine stowRoutine = new ParallelRoutine(
			new TimedRoutine(1),
			new IndexerFeedAllRoutine(),
			new IntakeStowRoutine());

	// TODO: add in a shooter routine here
	ParallelRoutine shootBallsRoutine = new ParallelRoutine(
			new TimedRoutine(0.3),
			new TimedRoutine(0.0));
}
