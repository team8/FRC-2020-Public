package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;

/**
 * @author Nolan Needs to be tested, add in starting points.
 */
@SuppressWarnings ("Duplicates")
public class StartCenterTwoPointTurnFriendlyTrenchThreeRendezvousTwo extends AutoBase {

	//TODO: test
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var trench1 = new DrivePathRoutine(newWaypoint(25, -35, 90));

		var trench2 = new DrivePathRoutine(newWaypoint(80, 45, 0), newWaypoint(170, 45, 0));

		var turnRoutine = new DriveYawRoutine(-180);
		var getRendezvous1 = new DrivePathRoutine(
				newWaypoint(100, -8, -65));

		return new SequentialRoutine(initialOdometry, trench1.driveInReverse(), getBallsRoutine, trench2,
				stowRoutine, turnRoutine, getRendezvous1,
				new DriveAlignYawAssistedRoutine(180, kOneTimesZoomPipelineId));
	}
}
