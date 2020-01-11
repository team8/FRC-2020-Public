package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.AbstractConfig;
import com.palyrobotics.frc2020.util.control.SmartGains;

public class ClimberConfig extends AbstractConfig {
    public double gravityFeedForward;

    public float kClimberMaxHeight;
    public float kClimberInchesPerRevolution;
    public float kClimberInchesPerMinutePerRpm;

    public SmartGains gains;
}
