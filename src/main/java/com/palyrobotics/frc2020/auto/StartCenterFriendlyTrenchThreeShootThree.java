package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kTwoTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

/**
 * Start by aligning to the tower target, then backing up slowly and also make sure that just the
 * front bumper extends past the white line.
 *
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterFriendlyTrenchThreeShootThree extends AutoBase {

	// Starts center, initial 180
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var getTrenchBalls = new DrivePathRoutine(
				newWaypoint(40, 55, 0),
				newWaypoint(170, 55, 0));

		return new SequentialRoutine(initialOdometry, getBallsRoutine, getTrenchBalls, stowRoutine,
				new DriveAlignYawAssistedRoutine(1000, kTwoTimesZoomPipelineId), // TODO: why is this 1000?
				shootBallsRoutine);
	}
}
