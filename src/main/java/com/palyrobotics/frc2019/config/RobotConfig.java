package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.util.config.AbstractConfig;

import java.util.List;

public class RobotConfig extends AbstractConfig {

    public boolean
            coastDriveIfDisabled, coastElevatorIfDisabled, coastArmIfDisabled,
            disableSparkOutput;
    public double smartMotionMultiplier; // Smart motion acceleration and velocity are multiplied by this. Useful for testing at lower speeds.
    public List<String> enabledServices, enabledSubsystems;
}
