package com.palyrobotics.frc2019.config.Constants;

public class PusherConstants {
    /**
     * Pusher
     */
    public static final int kVidarRequiredUltrasonicCount = 0;
    public static final double kVidarDistanceIn = 0.0;
    public static final double kVidarDistanceMiddle = 2.5;
    public static final double kVidarDistanceOut = 4.0;
    public static final double kVidarCargoTolerance = 3.500;
    public static final double kVidarDistanceCompress = 1;

    public static final double kMaxAngle = 120; //TODO: not sure if this is right
    public static final double kMaxAngleTicks = 0;

    /**
     * Tolerances
     */
    public static final double kAcceptablePositionError = .75;

    /**
     * Unit Conversions
     */
    public static final double kPusherInchesPerRotation = (1.0 * Math.PI); // TODO: change the 1 to the actual sprocket size
    public static final double kPusherEncSpeedUnitConversion = kPusherInchesPerRotation / 60; // RPM -> in/s
    public static final double kTicksPerInch = 42.0 / (1.0 * Math.PI); // todo: change the 1 to the actual sprocket size
    public static final double kPusherPotSpeedUnitConversion = (1.0 / kTicksPerInch) / OtherConstants.deltaTime; // ticks/20ms -> in/s
    public static final double kPusherPotentiometerTicksPerDegree = 4096.0 / (360.0 * 10.0); //TODO: fix this
}
