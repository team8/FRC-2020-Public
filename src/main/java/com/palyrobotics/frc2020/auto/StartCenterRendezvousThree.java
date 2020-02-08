package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterRendezvousThree extends RendezvousRoutine {

	//TODO: fix this, needs clear starting pos documented along with fixing the pathing. Also var names need a change

	@Override
	public RoutineBase getRoutine() {

		return CenterRendezvousThree();
	}
}
