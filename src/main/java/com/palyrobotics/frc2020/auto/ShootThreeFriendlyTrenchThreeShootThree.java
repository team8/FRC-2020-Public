package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.InitializeOdometryRoutine;

@SuppressWarnings ("Duplicates")
public class ShootThreeFriendlyTrenchThreeShootThree extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {

		var getTrenchBalls = new DrivePathRoutine(newPose(40, 55, 0), newPose(170, 55, 0));

		var turnAroundToShoot = new DrivePathRoutine(newPose(165, 60, 180));

		var initialOdometry = new InitializeOdometryRoutine(0, 0, 180);

		return new SequentialRoutine(initialOdometry, getTrenchBalls, turnAroundToShoot);
	}
}
