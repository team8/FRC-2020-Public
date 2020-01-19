package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;

public class ParallelDrivePathRoutine extends ParallelRoutine { // TODO implement class

	private final RoutineBase mRoutine;
	private final DrivePathRoutine mDrivePathRoutine;
	private final double mDistanceUntilEnd;

	public ParallelDrivePathRoutine(RoutineBase routine, DrivePathRoutine drivePathRoutine, double distanceUntilEnd) {
		super(routine, drivePathRoutine);
		mRoutine = routine;
		mDrivePathRoutine = drivePathRoutine;
		mDistanceUntilEnd = distanceUntilEnd;
	}
}
