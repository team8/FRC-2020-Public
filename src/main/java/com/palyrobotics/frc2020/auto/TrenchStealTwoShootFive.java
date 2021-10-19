package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypointMeters;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;

import edu.wpi.first.wpilibj.util.Units;

/**
 * Start by facing and aligning the center of the intake to the middle of the two balls of the
 * opponent's trench. Pull the robot back until just the back bumper covers the initiation line. The
 * left ball should be centered in the intake from this configuration.
 */
@SuppressWarnings ("Duplicates")
public class TrenchStealTwoShootFive extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);
//
		var getTrenchBalls = new ParallelRaceRoutine(
				new SequentialRoutine(
						new DrivePathRoutine(newWaypointMeters(Units.inchesToMeters(92), 0, 0))
								.setMovement(1.5, 2.4),
						new DriveYawRoutine(15.0)));
//
		var goToShoot = new DrivePathRoutine(newWaypointMeters(Units.inchesToMeters(50), Units.inchesToMeters(150), 180))
				.setMovement(5.0, 8.0)
				.driveInReverse();

		return new SequentialRoutine(setInitialOdometry, getTrenchBalls, goToShoot);
	}
}
