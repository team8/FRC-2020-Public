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
    public static final double kTicksPerInch = 42 / (2.00 * Math.PI) * (50/12)*(52/26)*(28/44);
    public static final double kClimberTicksPerInch = 0;
}
