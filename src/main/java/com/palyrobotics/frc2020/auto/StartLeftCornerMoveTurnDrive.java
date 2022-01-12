package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.robot.OperatorInterface;

// 4, 3, left, move 3, turn right

public class StartLeftCornerMoveTurnDrive extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);
		var sequentialRoutine = new SequentialRoutine(
				new DrivePathRoutine(
						newWaypoint(36, 0, 0),
						newWaypoint(84, 0, 0))
								.setMovement(6, 4));

		var turnRoutine = new DriveAlignYawAssistedRoutine(90, OperatorInterface.kOnesTimesZoomAlignButton);

		var secondSequentialRoutine = new DrivePathRoutine(
				newWaypoint(132, 0, 90))
					.setMovement(6,4);

		return new SequentialRoutine(setInitialOdometry, turnRoutine, secondSequentialRoutine);
	}
}
