package com.palyrobotics.frc2020.util;

import edu.wpi.first.wpilibj.Timer;

/**
 * Interpolating tree maps are used to get values at points that are not defined by making a guess
 * from points that are defined using linear interpolation.
 */
public class CircularInterpolatingDoubleTreeMap extends InterpolatingDoubleTreeMap {

	private final int mWindowSize;

	public CircularInterpolatingDoubleTreeMap(int windowSize) {
		mWindowSize = windowSize;
		while (size() < mWindowSize) {
			put(Timer.getFPGATimestamp(), 0.0);
		}
	}

	@Override
	public Double put(Double key, Double value) {
		while (size() > mWindowSize) {
			remove(firstKey());
		}
		return super.put(key, value);
	}
}
