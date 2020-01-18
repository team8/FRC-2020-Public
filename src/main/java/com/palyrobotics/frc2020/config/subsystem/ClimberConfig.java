package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

public class ClimberConfig extends SubsystemConfigBase {

	public float kClimberMaxHeight;
	public float kClimberInchesPerRevolution;
	public float kClimberInchesPerMinutePerRpm;

	public double acceptablePositionError;
	public double acceptableVelocityError;

	public double maxVelocity;

	public double adjustingOutput;

	public ProfiledGains gains;
}
