package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.util.Util;

public interface AutoBase {

	RoutineBase getRoutine();

	default String getName() {
		return Util.classToJsonName(getClass());
	}
}
