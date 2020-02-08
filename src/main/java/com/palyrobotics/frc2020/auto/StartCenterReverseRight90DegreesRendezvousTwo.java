package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;

/**
 * @author Nolan Needs editing/ revision
 */
@SuppressWarnings ("Duplicates")
public class StartCenterReverseRight90DegreesRendezvousTwo extends EndRendezvousTwoRoutine {

	//TODO: needs testing.
	@Override
	public RoutineBase getRoutine() {

		return new SequentialRoutine(CenterReverseRight90DegreeFriendlyTrench(),
				endRoutine());

	}
}
