package com.palyrobotics.frc2019.util.trajectory;

/**
 * A Double that can be interpolated using the InterpolatingTreeMap.
 *
 * @see InterpolatingTreeMap
 */
public class InterpolatingDouble implements Interpolable<InterpolatingDouble>, InverseInterpolable<InterpolatingDouble>, Comparable<InterpolatingDouble> {
    public Double value;

    public InterpolatingDouble(Double value) {
        this.value = value;
    }

    @Override
    public InterpolatingDouble interpolate(InterpolatingDouble other, double x) {
        double dydx = other.value - value;
        Double searchY = dydx * x + value;
        return new InterpolatingDouble(searchY);
    }

    @Override
    public double inverseInterpolate(InterpolatingDouble upper, InterpolatingDouble query) {
        double upperToLower = upper.value - value;
        if (upperToLower <= 0) {
            return 0;
        }
        double queryToLower = query.value - value;
        if (queryToLower <= 0) {
            return 0;
        }
        return queryToLower / upperToLower;
    }

    @Override
    public int compareTo(InterpolatingDouble other) {
		return value.compareTo(other.value);
    }

}