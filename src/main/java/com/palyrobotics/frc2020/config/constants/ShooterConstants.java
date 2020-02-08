package com.palyrobotics.frc2020.config.constants;

import java.util.TreeMap;

import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.util.InterpolatingDoubleTreeMap;

public class ShooterConstants {

	private ShooterConstants() {
	}

	public static final InterpolatingDoubleTreeMap kTargetDistanceToVelocity = new InterpolatingDoubleTreeMap();
	public static final TreeMap<Double, Shooter.HoodState> kTargetDistanceToHoodState = new TreeMap<>();
	public static final double kTimeToShootPerBallSeconds = 1.0;

	static {
		// TODO: add actual values from empirical measurements
		kTargetDistanceToVelocity.put(0.0, 0.0);
		kTargetDistanceToVelocity.put(300.0, 6000.0);
		// TODO: config?
		kTargetDistanceToHoodState.put(0.0, Shooter.HoodState.LOW);
		kTargetDistanceToHoodState.put(100.0, Shooter.HoodState.MIDDLE);
		kTargetDistanceToHoodState.put(200.0, Shooter.HoodState.HIGH);
	}
}
