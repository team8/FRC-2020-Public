package com.palyrobotics.frc2020.util;

import java.util.TreeMap;

/**
 * Interpolating tree maps are used to get values at points that are not defined by making a guess
 * from points that are defined using linear interpolation.
 */
public class InterpolatingDoubleTreeMap extends TreeMap<Double, Double> {

	public double interpolate(double low, double high, double normalizedValue) {
		if (high < low) {
			throw new IllegalArgumentException("Upper bound was not greater or equal to lower bound!");
		}
		double delta = high - low;
		return delta * normalizedValue + low;
	}

	public double inverseInterpolate(double low, double high, double interpolatedValue) {
		double upperToLower = high - low;
		if (upperToLower <= 0.0) {
			return 0.0;
		}
		double interpolatedToLower = interpolatedValue - low;
		if (interpolatedToLower <= 0.0) {
			return 0.0;
		}
		return interpolatedToLower / upperToLower;
	}

	/**
	 * @param  key Lookup for a key (does not have to exist)
	 * @return     Interpolated value or null
	 */
	public Double getInterpolated(double key) {
		Double retrievedValue = get(key);
		if (retrievedValue == null) {
			// Get surrounding keys for interpolation
			Double upperKeyBound = ceilingKey(key), lowerKeyBound = floorKey(key);
			// If attempting interpolation at ends of tree, return the nearest data point
			if (upperKeyBound == null && lowerKeyBound == null) {
				return null;
			} else if (upperKeyBound == null) {
				return get(lowerKeyBound);
			} else if (lowerKeyBound == null) {
				return get(upperKeyBound);
			}
			// get surrounding values for interpolation
			Double upperValue = get(upperKeyBound), lowerValue = get(lowerKeyBound);
			return interpolate(lowerValue, upperValue, inverseInterpolate(lowerKeyBound, upperKeyBound, key));
		} else {
			return retrievedValue;
		}
	}
}
