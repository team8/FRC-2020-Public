package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterReverseRight90DegreesRendezvousTwo extends AutoBase {

	//TODO: needs testing.
	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);

		var reverse = new DrivePathRoutine(newWaypoint(40, 55, 0));

		var getTrenchBalls = new DrivePathRoutine(newWaypoint(170, 55, 0));

		return new SequentialRoutine(initialOdometry, reverse.driveInReverse(), getBallsRoutine, getTrenchBalls,
				stowRoutine,
				new VisionAlignRoutine(), shootBallsRoutine);
	}
}
