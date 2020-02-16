package com.palyrobotics.frc2020.config.constants;

import java.util.*;

import com.palyrobotics.frc2020.subsystems.Shooter.HoodState;
import com.palyrobotics.frc2020.util.InterpolatingDoubleTreeMap;

@SuppressWarnings ("squid:S2386")
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
		middleMap.put(97.368, 3000.0);
		middleMap.put(143.6, 3050.0);
		middleMap.put(173.0, 3060.0);
		middleMap.put(204.0, 3100.0);
		middleMap.put(224.776, 3100.0);
		middleMap.put(250.0, 3200.0);
		middleMap.put(260.0, 3400.0);
		middleMap.put(272.0, 3450.0);
		kTargetDistanceToVelocity.put(HoodState.MIDDLE, middleMap);
		/* High Velocities */
		var highMap = new InterpolatingDoubleTreeMap();
//		highMap.put(290.0, )
		highMap.put(307.0, 3910.0);
		highMap.put(380.0, 4380.0);
		kTargetDistanceToVelocity.put(HoodState.HIGH, highMap);
		/* Hood States */
		kTargetDistanceToHoodState.put(0.0, HoodState.LOW);
		kTargetDistanceToHoodState.put(95.0, HoodState.MIDDLE);
		kTargetDistanceToHoodState.put(275.0, HoodState.HIGH);
	}
}
