package com.palyrobotics.frc2019.util;

/**
 * This class holds a bunch of static methods and variables needed for mathematics
 */
public class MathUtil {

    /**
     * Neutralizes a value within a dead band
     *
     * @param value      Value to control dead band
     * @param deadBand Value of dead band
     * @return 0 if within dead band, otherwise values
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
     * @param from The first angle
     * @param to   The second angle
     * @return The change in angle from the first argument necessary to line up with the second. Always between -Pi and Pi
     */
    public static double getDifferenceInAngleRadians(double from, double to) {
        return boundAngleNegPiToPiRadians(to - from);
    }

    /**
     * Get the difference in angle between two angles.
     *
     * @param from The first angle
     * @param to   The second angle
     * @return The change in angle from the first argument necessary to line up with the second. Always between -180 and 180
     */
    public static double getDifferenceInAngleDegrees(double from, double to) {
        return boundAngleNeg180to180Degrees(to - from);
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
}
