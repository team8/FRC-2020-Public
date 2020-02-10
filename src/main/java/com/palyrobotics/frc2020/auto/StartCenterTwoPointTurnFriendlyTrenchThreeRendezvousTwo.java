package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;

/**
 * @author Nolan Tested! Works
 */
@SuppressWarnings ("Duplicates")
public class StartCenterTwoPointTurnFriendlyTrenchThreeRendezvousTwo extends EndRendezvousTwoRoutine {

	@Override
	public RoutineBase getRoutine() {
		return new SequentialRoutine(CenterTwoPointTurnFriendlyTrench(),
				endRoutine());
	}
}
