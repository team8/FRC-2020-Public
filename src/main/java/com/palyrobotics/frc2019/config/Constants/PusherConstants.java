package com.palyrobotics.frc2019.config.Constants;

public class PusherConstants {
    /**
     * Pusher
     */
    public static final int kVidarRequiredUltrasonicCount = 0;
    public static final double kVidarDistanceIn = -0.1;
    public static final double kVidarDistanceMiddle = 2.5;
    public static final double kVidarDistanceOut = 6.1;
    public static final double kVidarCargoTolerance = 5.900;
    public static final double kVidarCargoToleranceFar = 7.000;
    public static final double kVidarDistanceCompress = 1;

    /**
     * Tolerances
     */
    public static final double kAcceptablePositionError = .2;

    /**
     * Unit Conversions
     */
    public static final double kPusherInchesPerRotation = (1.0 * Math.PI); // TODO: change the 1 to the actual sprocket size
    public static final double kPusherEncSpeedUnitConversion = kPusherInchesPerRotation / 60; // RPM -> in/s
    public static final double kTicksPerInch = 42.0 / (1.0 * Math.PI); // todo: change the 1 to the actual sprocket size
    public static final double kPusherPotSpeedUnitConversion = (1.0 / kTicksPerInch) / OtherConstants.deltaTime; // ticks/20ms -> in/s
    public static final double kPusherPotentiometerTicksPerDegree = 4096.0 / (360.0 * 10.0); //TODO: fix this
}
