package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

@SuppressWarnings ("Duplicates")
public class MoveForwardTenMeters extends AutoBase {

	@Override
	public RoutineBase getRoutine() {

		var move = new ParallelRaceRoutine(
				new DrivePathRoutine(newWaypoint(92, 0, 0))
						.setMovement(1.5, 2.4));

		return move;
	}
}
