package com.palyrobotics.frc2019.config.configv2;

import com.palyrobotics.frc2019.config.SmartGains;
import com.palyrobotics.frc2019.util.configv2.AbstractSubsystemConfig;

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
            rocketExpelAngle,
            climbAngle;

    public double holdTolerance; // Number of degrees of tolerance on arm hold to move elevator

    /* Set Point Allowable Errors */
    public double
            acceptableAngularError,
            angularVelocityError,
            cargoInchTolerance,
            cargoCountRequired;

    public SmartGains gains = SmartGains.emptyGains, holdGains = SmartGains.emptyGains;

    /* Unit Conversion */
    public static final double
            kArmPotentiometerTicksPerDegree = (4.5 / 1.0) * ((1.0 / 5.0) / 360.0),
            kArmDegreesPerRevolution = (360.0) / ((68.0 / 14.0) * (38.0 / 18.0) * (36.0 / 14.0) * (54.0 / 12.0)), // rev → deg
            kArmDegreesPerMinutePerRpm = kArmDegreesPerRevolution; // rev/min → deg/min TODO fix once we figure out velocity conversion

}
