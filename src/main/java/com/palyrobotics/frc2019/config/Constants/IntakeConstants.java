package com.palyrobotics.frc2019.config.Constants;

public class IntakeConstants {
    /**
     * Intake Constants
     */
    public static final double kMotorVelocity = .63;
    public static final double kFastIntakingVelocity = 0.8;
    public static final double kDroppingVelocity = 1.0;
    public static final double kExpellingVelocity = -1.0;
    public static final double kVerySlowly = .03;
    public static final double kMedium = 0.2;

    public static final double kMaxAngle = 117.0; // 90 - 61.7 + 90
    public static final double kMaxAngleTicks = .4611; //FIX

    // FEED FORWARD CONSTANTS

    public static final double kGravityFF = .64;
    public static final double kAccelComp = 0;
    public static final double kCentripetalCoeff = 0;

    // INTAKING POSITIONS

    public static final double kIntakingPosition = 23.5; // degrees relatively to the plane of the field.
    public static final double kHoldingPosition = 59; // same relative angle as above
    public static final double kHandoffPosition = 116; // place where the drop to the elevator occurs
    public static final double kRocketExpelPosition = 84;
    public static final double kClimbPosition = -30;

    public static final double kHoldTolerance = 5; // number of deg of tolerance on arm hold to move elevator

    /**
     * Tolerances
     */
    public static final double kAcceptableAngularError = 3;
    public static final double kAngularVelocityError = 10;
    public static final double kCargoInchTolerance = 6.75;
    public static final double kCargoCountRequired = 3;

    /**
     * Unit Conversions
     */
    public static final double kArmPotentiometerTicksPerDegree = (4.5/1.0)*(0.2/360.0); // .2 comes from 1/5
    // Using the NEO built in Encoder, so we must account for reduction.  ~= 42 / 360 * 118
    public static final double kArmDegreesPerRevolution = (360.0) / ((68.0/14.0) * (38.0/18.0) * (36.0/14.0) * (54.0/12.0));
    public static final double kArmEncoderSpeedUnitConversion = kArmDegreesPerRevolution / 60.0; // RPM -> Degrees per Second
    //TODO: Fix below
    public static final double kIntakeTicksPerInch = 0;

}
