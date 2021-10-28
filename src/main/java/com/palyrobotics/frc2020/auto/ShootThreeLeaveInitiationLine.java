package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.*;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;

/**
 * Start with three balls, and align to the tower target or near it, facing the target. Front of
 * bumper should be on initiation line.
 */

//Functional

@SuppressWarnings ("Duplicates")
public class ShootThreeLeaveInitiationLine extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);
		var indexerDownSlight = new IndexerTimeRoutine(1, true);
		var initialShoot = new ParallelRoutine(
				new IntakeLowerRoutine(),
				new DriveAlignRoutine(0),
				new ShooterVisionRoutine(3.0),
				new SequentialRoutine(
						new TimedRoutine(0.8), // TODO: Modify IndexerFeedAllRoutine to wait only for initial shot
						new IndexerFeedAllRoutine(2.2, false, true)));
		var moveOffInitiationLine = new DrivePathRoutine(newWaypoint(50, 0, 0));

		return new SequentialRoutine(setInitialOdometry, initialShoot, moveOffInitiationLine);
	}
}
