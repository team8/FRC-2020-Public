package com.palyrobotics.frc2020.config.constants;

import com.palyrobotics.frc2020.util.InterpolatingDoubleTreeMap;

public class ShooterConstants {

	private ShooterConstants() {
	}

	public static final InterpolatingDoubleTreeMap kTargetDistanceToVelocity = new InterpolatingDoubleTreeMap();

	static {
		// TODO: add actual values from empirical measurements
		kTargetDistanceToVelocity.put(0.0, 0.0);
		kTargetDistanceToVelocity.put(1.0, 1.0);
	}
}
