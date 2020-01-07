package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.AbstractSubsystemConfig;

import java.util.List;

public class SpinnerConfig extends AbstractSubsystemConfig {
    public List<Integer> colorSensorRedRGB;
    public List<Integer> colorSensorYellowRGB;
    public List<Integer> colorSensorCyanRGB;
    public List<Integer> colorSensorGreenRGB;

    public double idleOutput;
    public double rotationOutput;
    public double positionOutput;
}
