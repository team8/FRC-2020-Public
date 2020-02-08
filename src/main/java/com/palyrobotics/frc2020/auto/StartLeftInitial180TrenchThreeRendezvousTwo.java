package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;

public class StartLeftInitial180TrenchThreeRendezvousTwo extends EndRendezvousTwoRoutine {

	@Override
	public RoutineBase getRoutine() {
		return new SequentialRoutine(Left180FriendlyTrench(), endRoutine());
	}
}
