package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;

/**
 * @author Nolan Tested, works
 */
@SuppressWarnings ("Duplicates")
public class StartCenterReverseRight90DegreesRendezvousTwo extends EndRendezvousTwoRoutine {

	@Override
	public RoutineBase getRoutine() {

		return new SequentialRoutine(CenterReverseRight90DegreeFriendlyTrench(),
				endRoutine());

	}
}
