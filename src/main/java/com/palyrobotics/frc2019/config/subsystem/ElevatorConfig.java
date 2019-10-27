package com.palyrobotics.frc2019.config.subsystem;

import com.palyrobotics.frc2019.util.config.AbstractSubsystemConfig;
import com.palyrobotics.frc2019.util.control.SmartGains;

public class ElevatorConfig extends AbstractSubsystemConfig {

    public double
            manualPowerMultiplier,
            elevatorHeight1,
            elevatorHatchHeight2,
            elevatorCargoHeight2,
            elevatorHeight3,
            secondStageCanStartMovingArm,
            acceptablePositionError,
            acceptableVelocityError,
            closedLoopZoneHeight,
            outOfClosedLoopZoneIdleDelayMs,
            feedForward;

    public SmartGains gains;

    public static final float kMaxHeightInches = -80.0f;

    private static final double kSpoolEffectiveDiameter = 2.9;
    public static final double
            kElevatorInchesPerRevolution = (kSpoolEffectiveDiameter * Math.PI) * (12.0 / 52.0) * (26.0 / 50.0) * (40.0 / 60.0), // rev → in
            kElevatorInchesPerMinutePerRpm = kElevatorInchesPerRevolution; // rev/min → in/min TODO fix once we figure out velocity conversion
}
