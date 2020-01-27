package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.SetOdometryRoutine;

@SuppressWarnings ("Duplicates")
public class ShootThreeFriendlyTrenchThreeShootThree extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {

		var initialOdometry = new SetOdometryRoutine(0, 0, 180);

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(40, 55, 0), newWaypoint(170, 55, 0));

		var turnAroundToShoot = new DrivePathRoutine(newWaypoint(165, 60, 180));

		return new SequentialRoutine(initialOdometry, getTrenchBalls, turnAroundToShoot);
	}
}
