package com.palyrobotics.frc2019.config.configv2;

import com.palyrobotics.frc2019.config.SmartGains;
import com.palyrobotics.frc2019.util.configv2.AbstractSubsystemConfig;

public class ElevatorConfig extends AbstractSubsystemConfig {

    public double
            elevatorCargoHeight3Inches,
            manualMaxPercentOut,
            elevatorCargoHeight2Inches, elevatorCargoBallHeight, elevatorCargoHeight1Inches,
            handOffHeight,
            secondStageCanStartMovingArm,
            acceptablePositionError,
            acceptableVelocityError,
            closedLoopZoneHeight,
            outOfClosedLoopZoneIdleDelayMs,
            feedForward;

    public SmartGains gains = SmartGains.emptyGains;

    public static final float kMaxHeightInches = -80.0f;

    private static final double kSpoolEffectiveDiameter = 2.9;
    public static final double
            kElevatorInchesPerRevolution = (kSpoolEffectiveDiameter * Math.PI) * (12.0 / 52.0) * (26.0 / 50.0) * (40.0 / 60.0), // rev → in
            kElevatorInchesPerMinutePerRpm = kElevatorInchesPerRevolution; // rev/min → in/min
}
