package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.AbstractConfig;

import java.util.List;

public class RobotConfig extends AbstractConfig {

    public boolean
            coastDriveIfDisabled,
            disableOutput;
    public double smartMotionMultiplier; // Smart motion acceleration and velocity are multiplied by this. Useful for testing at lower speeds.
    public List<String> enabledServices, enabledSubsystems;
}
