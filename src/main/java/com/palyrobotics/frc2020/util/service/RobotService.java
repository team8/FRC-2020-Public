package com.palyrobotics.frc2020.util.service;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;

public interface RobotService {

	default void start() {
	}

	default void update(@ReadOnly RobotState state, @ReadOnly Commands commands) {
	}

	default String getConfigName() {
		return Util.classToJsonName(getClass());
	}
}
