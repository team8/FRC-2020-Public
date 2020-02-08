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
 * @author Nolan
 */
@SuppressWarnings ("Duplicates")
public class StartCenterReverseRight90DegreesRendezvousTwo extends AutoBase {

	//TODO: needs testing.
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var reverse = new DrivePathRoutine(newWaypoint(40, 55, 0));

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(170, 55, 0));

		var turnRoutine = new DriveYawRoutine(-180);
		var getRendezvous1 = new DrivePathRoutine(
				newWaypoint(100, -8, -65));

		return new SequentialRoutine(initialOdometry, reverse.driveInReverse(), getBallsRoutine, getTrenchBalls,
				stowRoutine, turnRoutine, getRendezvous1,
				new DriveAlignYawAssistedRoutine(180, kOneTimesZoomPipelineId), shootBallsRoutine);
	}
}
