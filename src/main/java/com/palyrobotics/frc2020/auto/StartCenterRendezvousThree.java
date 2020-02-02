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

		var getRendezvousBall1 = new DrivePathRoutine(newWaypoint(80, -70, 0));
		var backup1 = new DrivePathRoutine(newWaypoint(60, -70, 0));

		var getRendezvousBall2 = new DrivePathRoutine(newWaypoint(85, -90, 0));

		var turn2 = new DriveYawRoutine(180);

		return new SequentialRoutine(initialOdometry, turn, getBallsRoutine, getRendezvousBall1,
				backup1.driveInReverse(), getRendezvousBall2, stowRoutine,
				turn2, new VisionAlignRoutine(), shootBallsRoutine);
	}
}
