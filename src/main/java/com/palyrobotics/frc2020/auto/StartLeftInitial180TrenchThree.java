package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartLeftInitial180TrenchThree extends AutoBase {

	//TODO: test
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var turnAround = new DriveYawRoutine(0);

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(170, 0, 0));

		var turnAroundToShoot = new DriveYawRoutine(180);

		return new SequentialRoutine(initialOdometry, turnAround, getBallsRoutine, getTrenchBalls, stowRoutine,
				turnAroundToShoot,
				new DriveAlignRoutine(1), shootBallsRoutine);
	}
}
