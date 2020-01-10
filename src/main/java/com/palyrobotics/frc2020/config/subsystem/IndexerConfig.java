package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.AbstractSubsystemConfig;
import com.palyrobotics.frc2020.util.control.SmartGains;

public class IndexerConfig extends AbstractSubsystemConfig {
    public double transferVelocity;

    public SmartGains gains;
}
