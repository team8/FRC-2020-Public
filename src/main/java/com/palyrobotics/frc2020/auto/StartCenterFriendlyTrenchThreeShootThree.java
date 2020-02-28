package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;
import com.palyrobotics.frc2020.robot.OperatorInterface;

/**
 * Start by aligning to the tower target, then backing up slowly and also make sure that just the
 * front bumper extends past the white line.
 */
@SuppressWarnings ("Duplicates")
public class StartCenterFriendlyTrenchThreeShootThree extends FriendlyTrenchRoutine {

	// Starts center, initial 180
	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 180);
		// TODO: Refactor into AutoBase
		var initialShoot = new ParallelRoutine(
				new IntakeLowerRoutine(),
				new ShooterVisionRoutine(4.0),
				new SequentialRoutine(
						new TimedRoutine(1), // TODO: Modify IndexerFeedAllRoutine to wait only for initial shot
						new IndexerFeedAllRoutine(3, false, false)));

		var turnAndGetBalls = new SequentialRoutine(
				new DrivePathRoutine(newWaypoint(30, -20, 90))
						.driveInReverse()
						.setMovement(2.0, 4.0),
				new ParallelRoutine(
						new IntakeBallRoutine(6),
						new IndexerTimeRoutine(6),
						new SequentialRoutine(
								new DrivePathRoutine(
										newWaypoint(50, 50, 45),
										newWaypoint(70, 70, 0))
												.setMovement(2.0, 4.0)
												.endingVelocity(1.0),
								new DrivePathRoutine(newWaypoint(170, 70, 0))
										.startingVelocity(1.0)
										.setMovement(1.0, 4.0))));

		var turnAndShoot = new SequentialRoutine(
				new DriveAlignYawAssistedRoutine(180, OperatorInterface.kOnesTimesZoomAlignButton),
				new ParallelRoutine(
						new ShooterVisionRoutine(4),
						new IndexerFeedAllRoutine(4, true, true)));

		return new SequentialRoutine(setInitialOdometry, initialShoot, turnAndGetBalls, turnAndShoot);
	}
}
