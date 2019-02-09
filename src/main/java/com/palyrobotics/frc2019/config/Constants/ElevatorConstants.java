package com.palyrobotics.frc2019.config.Constants;

public class ElevatorConstants {
    /**
     * Elevator Constants
     */
    public static final double kNominalUpwardsOutput = 0.1;
    public static final double kTopBottomDifferenceInches = 0.0;
    public static final double kHFXAcceptableError = 0.01;
    public static final double kBotomPositionInches = 0;
    public static final double kHoldVoltage = 0.11;

    public static double kUncalibratedManualPower = 0;
    public static double kClosedLoopManualControlPositionSensitivity = 500;//250;
    public static double kConstantDownPower =0;

    public static final double kCalibratePower = -0.28;

    public static final double kClimberSliderScale = 0;

    public static final double kElevatorCargoHeight3Inches = 83.5 - OtherConstants.kCarriageToCargoCenterInches - OtherConstants.kGroundToCarriageInches;
    public static final double kElevatorCargoHeight2Inches = 55.5 - OtherConstants.kCarriageToCargoCenterInches - OtherConstants.kGroundToCarriageInches;
    public static final double kElevatorCargoHeight1Inches = 27.5 - OtherConstants.kCarriageToCargoCenterInches - OtherConstants.kGroundToCarriageInches;

    /**
     * Tolerances
     */
    public static final double kAcceptablePositionError = 40;
    public static final double kAcceptableVelocityError = 0.01;
    public static final double kClimberAcceptablePositionError = 0;
    public static final double kClimberAcceptableVelocityError = 0;

    /**
     * Unit Conversions
     */
    public static final double kElevatorRotationsPerInch = 1.0 / (2.00 * Math.PI) * (12.0/52.0) * (26.0/50.0) * (44.0/22.0);
    public static final double kElevatorSpeedUnitConversion = (1.0 / kElevatorRotationsPerInch) / 60; // RPM -> in/s
    //TODO: Calculate climber rotations per inch
    public static final double kClimberRotationsPerInch = 1.0 / (2.00 * Math.PI) * (12/52) * (26/50) * (40/60) * (14/66);
    public static final double kClimberSpeedUnitConversion = (1.0 / kClimberRotationsPerInch) / 60.0; // RPM -> in/s
}
