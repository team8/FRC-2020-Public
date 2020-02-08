package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;

/**
 * @author Nolan Needs to be tested, add in starting points.
 */
@SuppressWarnings ("Duplicates")
public class StartCenterTwoPointTurnFriendlyTrenchThreeRendezvousTwo extends EndRendezvousTwoRoutine {

	//TODO: test
	@Override
	public RoutineBase getRoutine() {
		return new SequentialRoutine(CenterTwoPointTurnFriendlyTrench(),
				endRoutine());
	}
}
