package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
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

		var initialShoot = new SequentialRoutine(
				new ParallelRoutine(
						new IntakeLowerRoutine(),
						new ShooterVisionRoutine(5.0),
						new IndexerFeedAllRoutine(3.5, false, false)));

		var turnAndGetBalls = new SequentialRoutine(
				new DrivePathRoutine(newWaypoint(40, -35, 90)).driveInReverse(),
				new ParallelRoutine(
						new IntakeBallRoutine(6),
						new IndexerTimeRoutine(6),
						new DrivePathRoutine(
								newWaypoint(80, 70, 0),
								newWaypoint(170, 70, 0))));

		var turnAndShoot = new SequentialRoutine(
				new DriveAlignYawAssistedRoutine(180, OperatorInterface.kOnesTimesZoomAlignButton),
				new ParallelRoutine(
						new ShooterVisionRoutine(5),
						new IndexerFeedAllRoutine(5, true, true)));

		return new SequentialRoutine(setInitialOdometry, initialShoot, turnAndGetBalls, turnAndShoot);
	}
}
