package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

/* Author: Alexis */

@SuppressWarnings ("Duplicates")
public class StartCenterRendezvousThree extends AutoBase {

	//TODO: fix this, needs clear starting pos documented along with fixing the pathing. Also var names need a change

	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var turn = new DriveYawRoutine(-45);

		var getTrenchBalls1 = new DrivePathRoutine(newWaypoint(70, -70, 0));
		var backup1 = new DrivePathRoutine(newWaypoint(30, -70, 0));

		var getTrenchBalls2 = new DrivePathRoutine(newWaypoint(80, -90, 0));
		var backup2 = new DrivePathRoutine(newWaypoint(60, -90, 0));

		var turn2 = new DriveYawRoutine(180);

		return new SequentialRoutine(initialOdometry, turn, getTrenchBalls1, backup1.driveInReverse(), getTrenchBalls2,
				backup2.driveInReverse(), turn2, new VisionAlignRoutine());
	}
}
