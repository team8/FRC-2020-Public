package com.palyrobotics.frc2019.config.Constants;

public class ElevatorConstants {
    /**
     * Elevator Constants
     */
    public static final double kNominalUpwardsOutput = 0.1;
    public static final double kTopBottomDifferenceInches = 84.0;
    public static final double kTopPositionInches = 84.0;
    public static final double kBottomPositionInches = 0.0;
    public static final double kHoldVoltage = .725;

    public static double kUncalibratedManualPower = 10;
    public static double kClosedLoopManualControlPositionSensitivity = 500;//250;

    public static final double kCalibratePower = -0.28;

    public static final double kClimberSliderScale = 0;

    public static final double kElevatorCargoHeight3Inches = 83.5 - OtherConstants.kCarriageToCargoCenterInches - OtherConstants.kGroundToCarriageInches;
    public static final double kElevatorCargoHeight2Inches = 55.5 - OtherConstants.kCarriageToCargoCenterInches - OtherConstants.kGroundToCarriageInches + 5;
    public static final double kElevatorCargoHeight1Inches = 27.5 - OtherConstants.kCarriageToCargoCenterInches - OtherConstants.kGroundToCarriageInches;

    public static final double testHeight = 15;

    /**
     * CAM Climb Constants
     */

    public static final double kManualOutputPercentOutput = .5;

    /**
     * Tolerances
     */
    public static final double kAcceptablePositionError = 0.5;
    public static final double kAcceptableVelocityError = 1.0;
    public static final double kClimberAcceptablePositionError = 0;
    public static final double kClimberAcceptableVelocityError = 0;

    /**
     * Unit Conversions
     */
    public static final double kSpoolEffectiveDiameter = 1.912 + .125/10;
    public static final double kElevatorRotationsPerInch = (1.0 / (kSpoolEffectiveDiameter * Math.PI)) * (52.0/12.0) * (50.0/26.0) * (22.0/44.0);
    public static final double kElevatorSpeedUnitConversion = (1.0 / kElevatorRotationsPerInch) / 60; // RPM -> in/s

    public static final double kClimberRotationsPerInch = 1.0 / (kSpoolEffectiveDiameter * Math.PI) * (52.0/12.0) * (50.0/26.0) * (60.0/40.0) * (66.0/14.0);
    public static final double kClimberSpeedUnitConversion = (1.0 / kClimberRotationsPerInch) / 60.0; // RPM -> in/s
}
// 1.107
//