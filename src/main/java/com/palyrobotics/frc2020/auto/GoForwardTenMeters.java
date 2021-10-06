
package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

import edu.wpi.first.wpilibj.util.Units;

public class GoForwardTenMeters extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		return new SequentialRoutine(new DriveSetOdometryRoutine(), new DrivePathRoutine(newWaypoint(Units.metersToInches(10), 0, 0)));
	}
}
