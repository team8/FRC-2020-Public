package com.palyrobotics.frc2019.config.subsystem;

import com.palyrobotics.frc2019.util.config.AbstractSubsystemConfig;
import com.palyrobotics.frc2019.util.control.SmartGains;

public class IntakeConfig extends AbstractSubsystemConfig {

    /* Intake Config */
    public double
            motorVelocity,
            fastIntakeVelocity,
            droppingVelocity,
            expellingVelocity,
            verySlowly,
            medium;

    public double
            maxAngle, // 90 - 61.7 + 90
            potentiometerMaxAngleTicks;

    /* Feed Forward */
    public double
            gravityFF,
            accelerationCompensation,
            centripetalCoefficient,
            angleOffset; // Offset for center of mass

    /* Set Points */
    public double
            intakeAngle, // Degrees relatively to the plane of the field.
            holdAngle, // Same relative angle as above
            handOffAngle, // Place where the drop to the elevator occurs
            rocketExpelAngle;

    public double holdTolerance; // Number of degrees of tolerance on arm hold to move elevator

    /* Set Point Allowable Errors */
    public double
            acceptableAngularError,
            angularVelocityError,
            cargoInchTolerance;
    public int
            cargoCountRequired;

    public SmartGains gains, holdGains;

    public boolean useBrokenPotFix;

    /* Unit Conversion */
    public static final double
            kArmPotentiometerTicksPerDegree = (4.5 / 1.0) * ((1.0 / 5.0) / 360.0),
            kArmDegreesPerRevolution = (360.0) / ((68.0 / 14.0) * (38.0 / 18.0) * (36.0 / 14.0) * (54.0 / 12.0)), // rev → deg
            kArmDegreesPerMinutePerRpm = kArmDegreesPerRevolution; // rev/min → deg/min TODO fix once we figure out velocity conversion
}
