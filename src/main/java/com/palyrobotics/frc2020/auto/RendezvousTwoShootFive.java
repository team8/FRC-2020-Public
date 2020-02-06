package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

@SuppressWarnings ("Duplicates")
public class RendezvousTwoShootFive extends AutoBase {

//start in front of target but facing away from it
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 0);

		// TODO: possibly more clear naming?
		var turn1 = new DriveYawRoutine(-45);

		var getTrenchBalls1 = new DrivePathRoutine(newWaypoint(70, -70, 0));
		// var backup1 = new DrivePathRoutine(newWaypoint(50, -70, 0));

		var turn = new DriveYawRoutine(155);

		return new SequentialRoutine(initialOdometry, turn1, getTrenchBalls1, turn, new VisionAlignRoutine());
	}
}
