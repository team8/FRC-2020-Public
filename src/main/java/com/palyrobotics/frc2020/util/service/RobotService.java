package com.palyrobotics.frc2020.util.service;

import com.palyrobotics.frc2020.util.Util;

public abstract interface RobotService {

	default void start() {
	}

	default void update() {
	}

	default String getConfigName() {
		return Util.classToJsonName(getClass());
	}
}
