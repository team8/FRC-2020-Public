package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;

import java.util.List;

public class SpinnerConfig extends SubsystemConfigBase {

	public List<Integer> colorSensorRedRGB, colorSensorYellowRGB, colorSensorCyanRGB, colorSensorGreenRGB;
	public double idleOutput, rotationOutput, positionOutput;
}
