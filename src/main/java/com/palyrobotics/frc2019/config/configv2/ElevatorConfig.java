package com.palyrobotics.frc2019.config.configv2;

import com.palyrobotics.frc2019.util.configv2.AbstractSubsystemConfig;

public class ElevatorConfig extends AbstractSubsystemConfig {

    public double
            elevatorCargoHeight3Inches,
            manualMaxPercentOut,
            elevatorCargoHeight2Inches, elevatorCargoBallHeight, elevatorCargoHeight1Inches,
            handOffHeight,
            testHeight,
            secondStageCanStartMovingArm,
            acceptablePositionError,
            acceptableVelocityError,
            climberAcceptablePositionError,
            climberAcceptableVelocityError,
            a, v,
            p, i, d, f, ff;

    public static final float kMaxHeightInches = -80.0f;

    private static final double kSpoolEffectiveDiameter = 2.9;
    public static final double
            kElevatorInchPerRevolution = (1.0 / (kSpoolEffectiveDiameter * Math.PI)) * (52.0 / 12.0) * (50.0 / 26.0) * (60.0 / 40.0), // R → in
            kElevatorInchPerSecPerRpm = (1.0 / kElevatorInchPerRevolution) / 60.0; // RPM → in/s

//            kClimberRotationsPerInch = 1.0 / (kSpoolEffectiveDiameter * Math.PI) * (52.0 / 12.0) * (50.0 / 26.0) * (60.0 / 40.0) * (66.0 / 14.0), // R → in
//            kClimberSpeedUnitConversion = (1.0 / kClimberRotationsPerInch) / 60.0; // RPM → in/s
}
