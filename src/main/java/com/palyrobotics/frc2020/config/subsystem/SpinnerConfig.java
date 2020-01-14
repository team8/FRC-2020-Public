package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.AbstractSubsystemConfig;

import java.util.List;

public class SpinnerConfig extends AbstractSubsystemConfig {
    public double idleOutput;
    public double rotationOutput;
    public double positionOutput;
    public int rotationControlColorPassedCount; //goal number of colors to be passes to achieve > 3 and < 5 control panel rotations
}
