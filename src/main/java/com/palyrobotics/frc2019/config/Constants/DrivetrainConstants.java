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
    public static final double kDriveWheelDiameterInches = 6;
    public static final double kTrackLengthInches = 9.00;
    public static final double kTrackWidthInches = 24.625;
    public static final double kTrackEffectiveDiameter = (kTrackWidthInches * kTrackWidthInches + kTrackLengthInches * kTrackLengthInches) / kTrackWidthInches;
    public static final double kTrackScrubFactor = .9;
    public static final double kPathFollowingLookahead = 39.0;
    public static final double kPathFollowingMaxAccel = 120;
    public static final double kPathFollowingMaxVel = 140.0;
    //public static final double kPathFollowingMaxVel = 5 * kPathFollowingMaxAccel;
    public static final double kPathFollowingTolerance = 0.35;

    /*
     * Control loop constants for both robots
     */
    public static final double kTurnInPlacePower = .45; //for bang bang
    public static final double kVisionLookingForTargetCreepPower = 0.18;
    public static final double kDriveMaxClosedLoopOutput = 0.9;

    public static final double kVisionTargetThreshold = 5; //threshold before target cannot be seen //TODO: Change this threshold

    /**
     * Tolerances
     */
    public static final double kAcceptableDrivePositionError = 15;
    public static final double kAcceptableDriveVelocityError = 5;
    public static final double kAcceptableShortDrivePositionError = 1;
    public static final double kAcceptableShortDriveVelocityError = 3;
    public static final double kAcceptableTurnAngleError = 4;
    public static final double kAcceptableGyroZeroError = 3;
    public static final double kAcceptableEncoderZeroError = 50;

    /**
     * Unit Conversions
     */
    public static final double kDriveInchesPerRotation = kDriveWheelDiameterInches * Math.PI * (12.0/46.0) * (22.0/44.0);
    public static final double kDriveInchesPerDegree = 0.99 * 21.5 / 90.0;
    public static final double kDriveSpeedUnitConversion = kDriveInchesPerRotation / 60.0;

    public static final int kCurrentLimit = 36; // amps

    @Override
    public String toString() {
        return String.format("kQuickStopAccumulatorDecreaseRate %skQuickStopAccumulatorDecreaseThreshold %skNegativeInertiaScalar %skAlpha %skDriveInchesPerRotation %skDriveInchesPerDegree%skDriveSpeedUnitConversion %s", kQuickStopAccumulatorDecreaseRate, kQuickStopAccumulatorDecreaseThreshold, kNegativeInertiaScalar, kAlpha, kDriveInchesPerRotation, kDriveInchesPerDegree, kDriveSpeedUnitConversion);
    }
}
