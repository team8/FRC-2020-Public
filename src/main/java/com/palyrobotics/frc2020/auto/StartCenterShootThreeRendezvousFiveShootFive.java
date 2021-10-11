package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.ShooterVisionRoutine;

public class StartCenterShootThreeRendezvousFiveShootFive extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(126, 130, 180);

		var initialShoot = new SequentialRoutine(
				new DriveYawRoutine(230),
				new ParallelRoutine(
						new ShooterVisionRoutine(3.0),
						new SequentialRoutine(
								new TimedRoutine(0.8),
								new IndexerFeedAllRoutine(2.2, false, true))));

		var getBalls = new ParallelRoutine(new SequentialRoutine(
				new DrivePathRoutine(newWaypoint(211, 171, 19))
						.setMovement(1.6, 1.4),
				new DrivePathRoutine(newWaypoint(294, 210, -25))
						.setMovement(1.6, 1.4),
				new DrivePathRoutine(newWaypoint(298, 182, 85))
						.setMovement(1.6, 1.4),
				new DrivePathRoutine(newWaypoint(216, 147, 157))
						.setMovement(1.6, 1.4)),
				new IntakeBallRoutine(5));

		var shoot = new SequentialRoutine(
				new ShooterVisionRoutine(5),
				new SequentialRoutine(
						new TimedRoutine(0.2),
						new IndexerFeedAllRoutine(3.8, false, true)));

		return new SequentialRoutine(setInitialOdometry, initialShoot, getBalls, shoot);
	}
}
