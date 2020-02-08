package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterTwoPointTurnFriendlyTrenchThree extends FriendlyTrenchRoutine {

	//TODO: test
	@Override
	public RoutineBase getRoutine() {

		return new SequentialRoutine(CenterTwoPointTurnFriendlyTrench(), endRoutine());
	}
}
