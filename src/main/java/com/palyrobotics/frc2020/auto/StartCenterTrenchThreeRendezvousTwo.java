package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.*;

/**
 * @author Nolan
 */
public class StartCenterTrenchThreeRendezvousTwo extends EndRendezvousTwoRoutine {

	@Override
	public RoutineBase getRoutine() {
		var initialOdometry = new DriveSetOdometryRoutine(0, 0, 180);
		var getTrenchBalls = new DrivePathRoutine(
				newWaypoint(40, 55, 0),
				newWaypoint(170, 55, 0));
		var turnRoutine = new DriveYawRoutine(-180);
		var getRendezvous1 = new DrivePathRoutine(
				newWaypoint(100, -8, -65));

		return new SequentialRoutine(CenterStraightFriendlyTrench(),
				endRoutine());
	}
}
