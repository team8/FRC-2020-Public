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
		middleMap.put(95.0, 2820.0); // 3000
		middleMap.put(131.0, 2900.0); // 3020
		middleMap.put(152.0, 2950.0);
		kTargetDistanceToVelocity.put(HoodState.MIDDLE, middleMap);
		/* High Velocities */
		var highMap = new InterpolatingDoubleTreeMap();
		highMap.put(215.0, 3420.0); // 3450
		highMap.put(237.0, 3490.0); // 3520
		highMap.put(300.0, 3650.0); // 3720
		highMap.put(329.0, 3700.0); // 3820
		highMap.put(343.0, 4000.0); // 4400
		highMap.put(360.0, 4100.0); // 4450
		kTargetDistanceToVelocity.put(HoodState.HIGH, highMap);
		/* Hood States */
		kTargetDistanceToHoodState.put(0.0, HoodState.LOW);
		kTargetDistanceToHoodState.put(60.0, HoodState.MIDDLE);
		kTargetDistanceToHoodState.put(215.0, HoodState.HIGH);
	}
}
