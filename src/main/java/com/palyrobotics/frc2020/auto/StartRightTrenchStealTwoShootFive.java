package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartRightTrenchStealTwoShootFive extends AutoBase {

	//TODO: test
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 0);

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(95, 0, 0));

		var goToShoot = new DrivePathRoutine(newWaypoint(100, 75, 180)).driveInReverse();

		var getRendezvous1 = new DrivePathRoutine(newWaypoint(90, 115, 30));
		var backup = new DrivePathRoutine(newWaypoint(40, 115, 30)).driveInReverse();
		//TODO add backup
		var getRendezvous2 = new DrivePathRoutine(newWaypoint(70, 140, 10));

		return new SequentialRoutine(initialOdometry, getTrenchBalls, goToShoot,
				new DriveAlignYawAssistedRoutine(170, kOneTimesZoomPipelineId),
				getRendezvous1, backup, getRendezvous2,
				new DriveAlignYawAssistedRoutine(175, kOneTimesZoomPipelineId));
	}
}
