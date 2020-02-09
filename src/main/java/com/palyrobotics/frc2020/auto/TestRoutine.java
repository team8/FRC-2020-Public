package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

public class TestRoutine extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		return new SequentialRoutine(new DriveSetOdometryRoutine(0, 0, 0),
				new DrivePathRoutine(newWaypoint(100, 0, 0)));
//				new DrivePathRoutine(newWaypoint(100, -100, -90)));
	}
}
