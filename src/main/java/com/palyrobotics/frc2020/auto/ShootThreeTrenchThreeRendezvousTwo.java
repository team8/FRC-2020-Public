package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

/* Author: Nolan */

public class ShootThreeTrenchThreeRendezvousTwo extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var init = new DriveSetOdometryRoutine(0, 0, 180);
		var getTrenchBalls = new DrivePathRoutine(newWaypoint(40, 55, 0), newWaypoint(170, 55, 0));
		var turnRoutine = new DriveYawRoutine(180);
		var getRendezvous1 = new DrivePathRoutine(newWaypoint(100, -3, 155));

		return new SequentialRoutine(init, getBallsRoutine, getTrenchBalls, turnRoutine, getRendezvous1,
				new DriveYawRoutine(180),
				new VisionAlignRoutine());
	}
}
