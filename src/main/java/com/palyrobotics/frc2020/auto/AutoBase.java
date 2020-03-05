package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.util.Util;

public abstract class AutoBase {

	public abstract RoutineBase getRoutine();

	public String getName() {
		return Util.classToJsonName(getClass());
	}

}
