package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.ConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class VisionConfig extends ConfigBase {

	public Gains gains;
	public double acceptableError;
}
