package com.palyrobotics.frc2020.util;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

/**
 * This class holds a bunch of static methods and variables needed for mathematics
 */
public class Util {

	public static final double kEpsilon = 1e-4;

	private Util() {
	}

	public static Pose2d newWaypoint(double xInches, double yInches, double yawDegrees) {
		return new Pose2d(Units.inchesToMeters(xInches), Units.inchesToMeters(yInches),
				Rotation2d.fromDegrees(yawDegrees));
	}

	/**
	 * Neutralizes a value within a dead band
	 *
	 * @param  value    Value to control dead band
	 * @param  deadBand Value of dead band
	 * @return          0 if within dead band, otherwise values
	 */
	public static double handleDeadBand(double value, double deadBand) {
		return (Math.abs(value) > Math.abs(deadBand)) ? value : 0.0;
	}

	public static double clamp01(double value) {
		return clamp(value, -1.0, 1.0);
	}

	public static double clamp(double value, double minimum, double maximum) {
		return Math.min(maximum, Math.max(minimum, value));
	}

	/**
	 * Get the difference in angle between two angles.
	 *
	 * @param  from The first angle
	 * @param  to   The second angle
	 * @return      The change in angle from the first argument necessary to line up with the second.
	 *              Always between -Pi and Pi
	 */
	public static double getDifferenceInAngleRadians(double from, double to) {
		return boundAngleNegPiToPiRadians(to - from);
	}

	public static double boundAngleNegPiToPiRadians(double angle) {
		// Naive algorithm
		while (angle >= Math.PI) {
			angle -= 2.0 * Math.PI;
		}
		while (angle < -Math.PI) {
			angle += 2.0 * Math.PI;
		}
		return angle;
	}

	/**
	 * Get the difference in angle between two angles.
	 *
	 * @param  from The first angle
	 * @param  to   The second angle
	 * @return      The change in angle from the first argument necessary to line up with the second.
	 *              Always between -180 and 180
	 */
	public static double getDifferenceInAngleDegrees(double from, double to) {
		return boundAngleNeg180to180Degrees(to - from);
	}

	public static double boundAngleNeg180to180Degrees(double angle) {
		// Naive algorithm
		while (angle >= 180.0) {
			angle -= 360.0;
		}
		while (angle < -180.0) {
			angle += 360.0;
		}
		return angle;
	}

	public static double boundAngle0to360Degrees(double angle) {
		// Naive algorithm
		while (angle >= 360.0) {
			angle -= 360.0;
		}
		while (angle < 0.0) {
			angle += 360.0;
		}
		return angle;
	}

	public static double boundAngle0to2PiRadians(double angle) {
		// Naive algorithm
		while (angle >= 2.0 * Math.PI) {
			angle -= 2.0 * Math.PI;
		}
		while (angle < 0.0) {
			angle += 2.0 * Math.PI;
		}
		return angle;
	}

	public static boolean approximatelyEqual(double d1, double d2) {
		return withinRange(d1, d2, kEpsilon);
	}

	public static boolean withinRange(double d1, double d2, double tolerance) {
		return Math.abs(d1 - d2) < tolerance;
	}

	public static String classToJsonName(Class<?> clazz) {
		if (clazz.isAnonymousClass()) {
			return "anonymous" + clazz.getSuperclass().getSimpleName();
		}
		String className = clazz.getSimpleName();
		// Make first character lowercase to match JSON conventions
		return Character.toLowerCase(className.charAt(0)) + className.substring(1);
	}
}
