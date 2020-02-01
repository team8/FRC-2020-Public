package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

public class ShootThreeTrenchThreeRendezvousTwo extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {
	    var init = new DriveSetOdometryRoutine(0, 0,180 );
		var getTrenchBalls = new DrivePathRoutine(newWaypoint(40, 55, 0), newWaypoint(170, 55, 0));
		var turnRoutine = new DriveYawRoutine(180);
		var getRendezvous = new DrivePathRoutine(newWaypoint(135, 55, 180), newWaypoint(170 - 95, -5, 95));
		var reverse = new DrivePathRoutine(newWaypoint(65, 45, 180));

		// var turnAroundToShoot = new DriveYawRoutine(180.0);

		return new SequentialRoutine(init, getTrenchBalls, turnRoutine, getRendezvous, reverse, new VisionAlignRoutine());
	}
}
