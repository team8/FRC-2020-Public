package com.palyrobotics.frc2019.config.Constants;

public class DrivetrainConstants {
    /**
     * Cheesy Drive Constants Set by DriverProfiles
     */
    //Deadband for joysticks
    public static double kDeadband;
    public static double kMaxAccelRate;
    //Threshold for quickturn sensitivity change
    public static double kQuickTurnSensitivityThreshold;
    //Sensitivities for how fast non-quickturn turning is
    public static double kDriveSensitivity;
    //Sensitivities for quickturn
    public static double kQuickTurnSensitivity;
    public static double kPreciseQuickTurnSensitivity;
    //The rate at which the QuickStopAccumulator will decrease
    public static double kQuickStopAccumulatorDecreaseRate;
    //The value at which the QuickStopAccumulator will stop decreasing
    public static double kQuickStopAccumulatorDecreaseThreshold;
    public static double kNegativeInertiaScalar;
    //How much the QuickStopAccumulator is affected by the wheel
    //(1-alpha) is how much the QuickStopAccumulator is affected by the previous QuickStopAccumulator
    //Range: (0, 1)
    public static double kAlpha;
    public static double kCyclesUntilStop;

    //!!! Loop rate of normal Looper
    public static final double kNormalLoopsDt = 0.02;

    /**
     * Preprocessing constants
     */
    // Distance between the intermediate points inserted in the drive path; set to 0 to turn off insertion
    public static final double kInsertionSpacingInches = 6.0;
    // Smoothing constants
    public static final double kSmoothingWeight = 0.90; // 1 = max smoothness
    public static final double kSmoothingWeightData = 1 - kSmoothingWeight;
    public static final double kSmoothingTolerance = 0.001; // When the change in path falls below the threshold it is basically converged
    public static final double kSmoothingMaxIters = 10000;
    public static final double kTurnVelocityReduction = 2; // A constant for reducing following speed during turns; 1 for slowest turning, 5 for fastest
    public static final double kDriveWheelDiameterInches = 6.25;
    public static final double kTrackLengthInches = 8.2;
    public static final double kTrackWidthInches = 25.4;
    public static final double kTrackEffectiveDiameter = (kTrackWidthInches * kTrackWidthInches + kTrackLengthInches * kTrackLengthInches) / kTrackWidthInches;
    public static final double kTrackScrubFactor = .9;
    public static final double kPathFollowingLookahead = 35.0;
    public static final double kPathFollowingMaxAccel = 120;
    public static final double kPathFollowingMaxVel = 100.0;
    //public static final double kPathFollowingMaxVel = 5 * kPathFollowingMaxAccel;
    public static final double kPathFollowingTolerance = 0.20;

    /*
     * Control loop constants for both robots
     */
    public static final double kTurnInPlacePower = .5; //for bang bang
    public static final double kDriveMaxClosedLoopOutput = 1.0;

    /**
     * Tolerances
     */
    public static final double kAcceptableDrivePositionError = 15;
    public static final double kAcceptableDriveVelocityError = 5;
    public static final double kAcceptableShortDrivePositionError = 1;
    public static final double kAcceptableShortDriveVelocityError = 3;
    public static final double kAcceptableTurnAngleError = 5;
    public static final double kAcceptableGyroZeroError = 3;
    public static final double kAcceptableEncoderZeroError = 50;

    /**
     * Unit Conversions
     */
    public static final double kDriveTicksPerInch = 4096.0 / (6.25 * Math.PI);
    public static final double kDriveInchesPerDegree = 0.99 * 21.5 / 90.0;
    public static final double kDriveSpeedUnitConversion = 4096.0 / (6.25 * Math.PI * 10.0);


    @Override
    public String toString() {
        return "kQuickStopAccumulatorDecreaseRate " + kQuickStopAccumulatorDecreaseRate + "kQuickStopAccumulatorDecreaseThreshold "
                + kQuickStopAccumulatorDecreaseThreshold + "kNegativeInertiaScalar " + kNegativeInertiaScalar + "kAlpha " + kAlpha + "kDriveTicksPerInch "
                + kDriveTicksPerInch + "kDriveInchesPerDegree" + kDriveInchesPerDegree + "kDriveSpeedUnitConversion " + kDriveSpeedUnitConversion;
    }
}
