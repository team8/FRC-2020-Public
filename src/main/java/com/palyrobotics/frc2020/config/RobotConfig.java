package com.palyrobotics.frc2020.config;

import java.util.List;

import com.palyrobotics.frc2020.util.config.ConfigBase;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotConfig extends ConfigBase {

	public boolean coastDriveIfDisabled, disableHardwareUpdates, enableLimelightIfDisabled;

	// Useful for testing at lower speeds
	public double motorOutputMultiplier;

	public List<String> enabledServices, enabledSubsystems;
}
