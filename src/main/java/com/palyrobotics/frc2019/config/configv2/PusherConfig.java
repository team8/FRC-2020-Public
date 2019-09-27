package com.palyrobotics.frc2019.config.configv2;

import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.util.configv2.AbstractSubsystemConfig;

public class PusherConfig extends AbstractSubsystemConfig {

    /* Pusher */
    public int vidarRequiredUltrasonicCount;
    public double
            vidarDistanceIn,
            vidarDistanceMiddle,
            vidarDistanceOut,
            vidarCargoTolerance,
            vidarCargoToleranceFar,
            vidarDistanceCompress;

    /* Tolerances */
    public double acceptablePositionError;

    /* Unit Conversion */
    public static final double
            kPusherInchesPerRotation = (1.0 * Math.PI), // TODO: change the 1 to the actual sprocket size
            kPusherEncSpeedUnitConversion = kPusherInchesPerRotation / 60, // RPM -> in/s
            kTicksPerInch = 42.0 / (1.0 * Math.PI), // todo: change the 1 to the actual sprocket size
            kPusherPotSpeedUnitConversion = (1.0 / kTicksPerInch) / OtherConstants.deltaTime, // ticks/20ms -> in/s
            kPusherPotentiometerTicksPerDegree = 4096.0 / (360.0 * 10.0); //TODO: fix this
}
