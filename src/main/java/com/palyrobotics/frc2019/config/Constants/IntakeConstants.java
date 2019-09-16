package com.palyrobotics.frc2019.config.Constants;

public class IntakeConstants {
    /**
     * Intake Constants
     */
    public static final double
            kMotorVelocity = 0.58,
            kFastIntakingVelocity = 0.8,
            kDroppingVelocity = 1.0,
            kExpellingVelocity = -0.9,
            kVerySlowly = 0.03,
            kMedium = 0.2;

    public static final double
            kMaxAngle = 118.0, // 90 - 61.7 + 90
            kMaxAngleTicks = 0.6095; // TODO fix somehow (make command)

    // FEED FORWARD CONSTANTS

    public static final double
            kGravityFF = 0.03,
            kAccelerationCompensation = 0.0,
            kCentripetalCoefficient = 0.0,
            kAngleOffset = 11.89; // Offset for center of mass

    // INTAKING POSITIONS

    public static final double
            kIntakeAngle = 23.5, // Degrees relatively to the plane of the field.
            kHoldAngle = 59.0, // Same relative angle as above
            kHandOffAngle = 116.0, // Place where the drop to the elevator occurs
            kRocketExpelAngle = 84.0,
            kClimbAngle = -30.0,
            kLowestAngle = kIntakeAngle,
            kHighestAngle = kHandOffAngle;

    public static final double kHoldTolerance = 14.5; // Number of deg of tolerance on arm hold to move elevator

    /**
     * Tolerances
     */
    public static final double
            kAcceptableAngularError = 3,
            kAngularVelocityError = 10,
            kCargoInchTolerance = 7.65,
            kCargoCountRequired = 6;

    /**
     * Unit Conversions
     */
    public static final double kArmPotentiometerTicksPerDegree = (4.5 / 1.0) * (0.2 / 360.0); // .2 comes from 1/5
    // Using the NEO built in Encoder, so we must account for reduction.  ~= 42 / 360 * 118
    public static final double
            kArmDegreesPerRevolution = (360.0) / ((68.0 / 14.0) * (38.0 / 18.0) * (36.0 / 14.0) * (54.0 / 12.0)), // R → Degree
            kArmDegreePerSecPerRpm = kArmDegreesPerRevolution / 60.0; // RPM → Degrees per Second
}
