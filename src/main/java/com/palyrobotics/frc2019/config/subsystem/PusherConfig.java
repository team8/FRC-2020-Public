package com.palyrobotics.frc2019.config.subsystem;

import com.palyrobotics.frc2019.config.constants.OtherConstants;
import com.palyrobotics.frc2019.util.config.AbstractSubsystemConfig;
import com.palyrobotics.frc2019.util.control.Gains;
import com.palyrobotics.frc2019.util.control.SmartGains;

public class PusherConfig extends AbstractSubsystemConfig {

    public double
            distanceIn,
            distanceOut,
            cargoTolerance,
            cargoToleranceFar;
    public boolean useSlam;
    public double slamPercentOutput, slamHoldMultiplier, slamTime;

    public SmartGains smartGains;
    public Gains positionGains;

    /* Tolerances */
    public double acceptablePositionError, acceptableVelocityError;

    /* Unit Conversion */
    public static final double
            kPusherInchesPerRotation = (1.0 * Math.PI), // TODO: change the 1 to the actual sprocket size
            kPusherEncSpeedUnitConversion = kPusherInchesPerRotation, // RPM -> in/s
            kTicksPerInch = 42.0 / (1.0 * Math.PI), // todo: change the 1 to the actual sprocket size
            kPusherPotSpeedUnitConversion = (1.0 / kTicksPerInch) / OtherConstants.deltaTime, // ticks/20ms -> in/s
            kPusherPotentiometerTicksPerDegree = 4096.0 / (360.0 * 10.0); //TODO: fix this
}
