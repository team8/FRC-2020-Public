package com.palyrobotics.frc2020.config;

import java.util.List;

import com.palyrobotics.frc2020.util.config.ConfigBase;

public class RobotConfig extends ConfigBase {

	public boolean coastDriveIfDisabled, disableOutput;

	// Smart motion acceleration and velocity are multiplied by this. Useful for
	// testing at lower speeds.
	public double smartMotionMultiplier;

	public List<String> enabledServices, enabledSubsystems;
}
