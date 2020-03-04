package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;
import com.palyrobotics.frc2020.subsystems.Indexer;

/**
 * Start by facing and aligning the center of the intake to the middle of the two balls of the
 * opponent's trench. Pull the robot back until just the back bumper covers the initiation line. The
 * left ball should be centered in the intake from this configuration.
 */
@SuppressWarnings ("Duplicates")
public class TrenchStealTwoShootFiveRendezvousTwo extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);
		var getTrenchBalls = new ParallelRaceRoutine(
				new SequentialRoutine(
						new DrivePathRoutine(newWaypoint(92, 0, 0))
								.setMovement(1.5, 2.4),
						new DriveYawRoutine(15.0)),
				new IndexerTimeRoutine(Double.POSITIVE_INFINITY),
				new IntakeBallRoutine(Double.POSITIVE_INFINITY, 1.0));
		var goToShoot = new ParallelRaceRoutine(
				new DrivePathRoutine(newWaypoint(60, 70, 150))
						.setMovement(1.5, 2.4)
						.driveInReverse(),
				new IndexerTimeRoutine(Double.POSITIVE_INFINITY),
				new SequentialRoutine(
						new TimedRoutine(0.2),
						new IndexerHopperRoutine(Indexer.HopperState.OPEN),
						new TimedRoutine(Double.POSITIVE_INFINITY)));
		var shootBalls = new SequentialRoutine(
				new DriveAlignRoutine(0),
				new ParallelRoutine(
						new ShooterVisionRoutine(6),
						new SequentialRoutine(
								new TimedRoutine(1.0),
								new SequentialRoutine(
										new IndexerFeedAllRoutine(0.4, false, true),
										new IndexerFeedAllRoutine(4.6, false, true)))));
		var turnAndIntake = new ParallelRaceRoutine(
				new SequentialRoutine(
						new DrivePathRoutine(newWaypoint(77, 113, 15.0))
								.setMovement(2.0, 2.0),
						new DriveYawRoutine(20.0)),
				new IntakeBallRoutine(Double.POSITIVE_INFINITY),
				new IndexerTimeRoutine(Double.POSITIVE_INFINITY));
//        var backupAndShoot = new SequentialRoutine(
//                new DrivePathRoutine(newWaypoint(40.0, 113.0, 150.0))
//                        .driveInReverse(),
//                new ParallelRoutine(
//                        new DriveAlignRoutine(0),
//                        new ShooterVisionRoutine(6),
//                        new SequentialRoutine(
//                                new TimedRoutine(1.0),
//                                new SequentialRoutine(
//                                        new IndexerFeedAllRoutine(0.4, false, true),
//                                        new IndexerFeedAllRoutine(4.6, false, true)
//                                )))
//        );
//        return new SequentialRoutine(setInitialOdometry, getTrenchBalls, goToShoot, shootBalls, turnAndIntake, backupAndShoot);
		return new SequentialRoutine(setInitialOdometry, getTrenchBalls, goToShoot, shootBalls, turnAndIntake);
	}
}
