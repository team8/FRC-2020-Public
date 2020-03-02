package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import java.util.function.Predicate;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveParallelPathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;
import com.palyrobotics.frc2020.robot.OperatorInterface;
import com.palyrobotics.frc2020.subsystems.Shooter;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.util.Units;

/**
 * Start by aligning to the tower target, then backing up slowoly and also make sure that just the
 * front bumper extends past the white line.
 */
@SuppressWarnings ("Duplicates")
public class StartCenterFriendlyTrenchThreeShootThree extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 180);
		var initialShoot = new ParallelRoutine(
				new IntakeLowerRoutine(),
//				new ShooterCustomVelocityRoutine(3.0, ),
				new ShooterVisionRoutine(3.0),
				new SequentialRoutine(
						new TimedRoutine(1), // TODO: Modify IndexerFeedAllRoutine to wait only for initial shot
						new IndexerFeedAllRoutine(3, false, true)));
		Predicate<Pose2d> inTrenchTest = poseMeters -> poseMeters.getTranslation().getX() > Units.inchesToMeters(80.0);
		var turnAndGetBalls = new SequentialRoutine(
				new DrivePathRoutine(newWaypoint(10, -10, 90))
						.setMovement(2.0, 1.2)
						.driveInReverse(),
				new DriveParallelPathRoutine(
						new DrivePathRoutine(
								newWaypoint(50, 70, 0),
								newWaypoint(170, 70, 0))
										.setMovement(2.0, 1.2)
										// Slow down to intake balls
										.limitWhen(1.2, inTrenchTest),
						// Intake balls in trench
						new ParallelRoutine(
								new IntakeBallRoutine(6),
								new IndexerTimeRoutine(6)),
						inTrenchTest));

		var turnAndShoot = new SequentialRoutine(
				new ParallelRaceRoutine(
						new IndexerTimeRoutine(Double.POSITIVE_INFINITY),
						new ShooterCustomVelocityRoutine(Double.POSITIVE_INFINITY, 2000.0, Shooter.HoodState.HIGH),
						new DriveAlignYawAssistedRoutine(180, OperatorInterface.kOnesTimesZoomAlignButton)),
				new ParallelRoutine(
						new ShooterVisionRoutine(4.0),
						new SequentialRoutine(
								new TimedRoutine(0.2),
								new IndexerFeedAllRoutine(3.0, false, true))));

		return new SequentialRoutine(setInitialOdometry, initialShoot, turnAndGetBalls, turnAndShoot);
	}
}
