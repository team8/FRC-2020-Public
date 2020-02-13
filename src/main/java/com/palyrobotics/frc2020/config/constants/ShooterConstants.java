package com.palyrobotics.frc2020.config.constants;

import java.util.*;

import com.palyrobotics.frc2020.subsystems.Shooter.HoodState;
import com.palyrobotics.frc2020.util.InterpolatingDoubleTreeMap;

public class ShooterConstants {

	private ShooterConstants() {
	}

	public static final Map<HoodState, InterpolatingDoubleTreeMap> kTargetDistanceToVelocity = new EnumMap<>(HoodState.class);
	public static final NavigableMap<Double, HoodState> kTargetDistanceToHoodState = new TreeMap<>();
	public static final double kTimeToShootPerBallSeconds = 1.0;

	static {
		// TODO: config?
		/* Low Velocities */
		var lowMap = new InterpolatingDoubleTreeMap();
		lowMap.put(0.0, 0.0);
		kTargetDistanceToVelocity.put(HoodState.LOW, lowMap);
		/* Middle Velocities */
		var middleMap = new InterpolatingDoubleTreeMap();
		middleMap.put(130.0, 1750.0);
		middleMap.put(180.0, 1850.0);
		middleMap.put(200.0, 1885.0);
		kTargetDistanceToVelocity.put(HoodState.MIDDLE, middleMap);
		/* High Velocities */
		var highMap = new InterpolatingDoubleTreeMap();
		highMap.put(0.0, 0.0);
		kTargetDistanceToVelocity.put(HoodState.HIGH, highMap);
		/* Hood States */
		kTargetDistanceToHoodState.put(0.0, HoodState.LOW);
		kTargetDistanceToHoodState.put(100.0, HoodState.MIDDLE);
		kTargetDistanceToHoodState.put(200.0, HoodState.HIGH);
	}
}
