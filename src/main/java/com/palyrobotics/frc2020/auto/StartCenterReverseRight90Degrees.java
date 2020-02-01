package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

@SuppressWarnings ("Duplicates")
public class StartCenterReverseRight90Degrees extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var reverse = new DrivePathRoutine(newWaypoint(40, 55, 0));

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(170, 55, 0));

		var turnAroundToShoot = new DriveYawRoutine(180.0);

		return new SequentialRoutine(initialOdometry, reverse.driveInReverse(), getTrenchBalls,
				turnAroundToShoot, new VisionAlignRoutine());
	}
}
