package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterShootThreeRendezvousFiveTrenchThree extends EndRendezvousTwoRoutine {

	@Override
	public RoutineBase getRoutine() {

		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);
		var turn = new DriveYawRoutine(-45);

		var getRendezvous1 = new DrivePathRoutine(newWaypoint(70, -40, 0));
		var backup1 = new DrivePathRoutine(newWaypoint(30, -40, 0)).driveInReverse();
		var getRendezvous2 = new DrivePathRoutine(newWaypoint(140, -30, 30));

		var turn2 = new DriveYawRoutine(90);
		var getRendezvous3 = new DrivePathRoutine(newWaypoint(140, 20, 90));

		return new SequentialRoutine(initialOdometry, turn, getRendezvous1, backup1, getRendezvous2, turn2, getRendezvous3,
				new VisionAlignRoutine());

	}
}
