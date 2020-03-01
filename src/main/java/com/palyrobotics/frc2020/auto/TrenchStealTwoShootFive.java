package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerTimeRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.ShooterVisionRoutine;

/**
 * Start by facing and aligning your left wheel to cover the trench. Pull the robot back until just
 * the back bumper covers the initiation line. The left ball should be centered in the intake from
 * this configuration.
 *
 * @author Jason
 */
@SuppressWarnings ("Duplicates")
public class TrenchStealTwoShootFive extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);

		var getTrenchBalls = new ParallelRoutine(
				new SequentialRoutine(
						new DrivePathRoutine(newWaypoint(92, 0, 0))
								.setMovement(2.7, 2.4),
						new DrivePathRoutine(newWaypoint(76, 0, 0))
								.setMovement(2.7, 2.4)
								.driveInReverse(),
						new DrivePathRoutine(newWaypoint(92, -10, -25))
								.setMovement(2.7, 2.4)),
				new IndexerTimeRoutine(6),
				new IntakeBallRoutine(6));

		var goToShoot = new DrivePathRoutine(newWaypoint(50, 150, 190))
				.setMovement(3.0, 3.3)
				.driveInReverse();

		var shootBalls = new SequentialRoutine(
				new DriveAlignRoutine(0),
				new ParallelRoutine(
						new ShooterVisionRoutine(6),
						new IndexerFeedAllRoutine(6, true, true)));

		return new SequentialRoutine(setInitialOdometry, getTrenchBalls, goToShoot, shootBalls);
	}
}
