package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawAlignRoutine;

@SuppressWarnings ("Duplicates")
public class ShootThreeFriendlyTrenchThreeShootThree extends AutoBase {

	// Starts center, initial 180
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(40, 55, 0), newWaypoint(170, 55, 0));

		return new SequentialRoutine(initialOdometry, getBallsRoutine, getTrenchBalls, stowRoutine,
				new DriveYawAlignRoutine(1000),
				shootBallsRoutine);
	}
}
