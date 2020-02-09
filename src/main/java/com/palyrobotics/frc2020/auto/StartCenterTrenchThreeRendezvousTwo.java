package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.*;

/**
 * @author Nolan Tested
 */
public class StartCenterTrenchThreeRendezvousTwo extends EndRendezvousTwoRoutine {

	@Override
	public RoutineBase getRoutine() {

		return new SequentialRoutine(CenterStraightFriendlyTrench(),
				endRoutine());
	}
}
