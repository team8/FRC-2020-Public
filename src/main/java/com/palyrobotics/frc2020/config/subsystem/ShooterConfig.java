package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;

public class ShooterConfig extends SubsystemConfigBase {

	public Gains velocityGains;
	public double maxVelocity, velocityTolerance;
	public double rumbleDurationSeconds;
}
