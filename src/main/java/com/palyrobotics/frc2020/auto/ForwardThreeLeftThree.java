package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

@SuppressWarnings ("Duplicates")
public class ForwardThreeLeftThree extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);
		var moveForwardTurnLeft = new DrivePathRoutine(newWaypoint(36, 0, 90));
		var forwardThree = new DrivePathRoutine(newWaypoint(0, 36, 90));
		var forwardThreeLeftThree = new SequentialRoutine(moveForwardTurnLeft, forwardThree);
		return new SequentialRoutine(setInitialOdometry, forwardThreeLeftThree);
	}
}
