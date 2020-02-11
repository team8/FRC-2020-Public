package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;


import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

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

		var getRendezvous1 = new DrivePathRoutine(newWaypoint(100, 115, 10));
		//TODO> add backup
		var getRendezvous2 = new DrivePathRoutine(newWaypoint(95, 130, 10));



		return new SequentialRoutine(initialOdometry, getTrenchBalls, goToShoot,
				new DriveAlignYawAssistedRoutine(150, kOneTimesZoomPipelineId),
				getRendezvous1, getRendezvous2,
				new DriveAlignYawAssistedRoutine(150, kOneTimesZoomPipelineId)
		);
	}
}
