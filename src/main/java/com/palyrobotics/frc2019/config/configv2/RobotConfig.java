package com.palyrobotics.frc2019.config.configv2;

import com.palyrobotics.frc2019.util.configv2.AbstractConfig;

import java.util.ArrayList;
import java.util.List;

public class RobotConfig extends AbstractConfig {

    public boolean competitionMode, disabledUseBrakeMode;
    public double sendMultiplier = 1.0;
    public List<String> enabledServices = new ArrayList<>(), enabledSubsystems = new ArrayList<>();
}
