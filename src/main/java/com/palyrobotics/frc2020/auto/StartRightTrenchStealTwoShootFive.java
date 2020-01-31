package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;

@SuppressWarnings ("Duplicates")
public class StartRightTrenchStealTwoShootFive extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {

		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(40, 0, 0));

		var goToShoot = new DrivePathRoutine(newWaypoint(0, 80, 120));

		var turnAroundToShoot = new DriveYawRoutine(180.0);

		return new SequentialRoutine(initialOdometry, getTrenchBalls, goToShoot, turnAroundToShoot);
	}
}
