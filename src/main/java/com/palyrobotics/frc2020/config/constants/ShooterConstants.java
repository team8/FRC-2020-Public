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
		middleMap.put(95.0, 3000.0);
		middleMap.put(131.0, 3020.0);
		middleMap.put(152.0, 3030.0);
		kTargetDistanceToVelocity.put(HoodState.MIDDLE, middleMap);
		/* High Velocities */
		var highMap = new InterpolatingDoubleTreeMap();
		highMap.put(215.0, 3400.0);
		highMap.put(237.0, 3550.0);
		highMap.put(300.0, 3980.0);
		highMap.put(350.0, 4390.0);
//		highMap.put(215.0, 3350.0);
//		highMap.put(237.0, 3500.0);
//		highMap.put(300.0, 3930.0);
//		highMap.put(350.0, 4340.0); // 367+
		kTargetDistanceToVelocity.put(HoodState.HIGH, highMap);
		/* Hood States */
		kTargetDistanceToHoodState.put(0.0, HoodState.LOW);
		kTargetDistanceToHoodState.put(60.0, HoodState.MIDDLE);
		kTargetDistanceToHoodState.put(215.0, HoodState.HIGH);
	}
}
