package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;

public class TurretConfig extends SubsystemConfigBase {

	public double rotatingOutput;
	public double acceptableYawError;
	public double maximumAngle;
	public double distanceToMiddleOfField;
	public Gains turnGains;
	public double maxRotationOutput;
}
