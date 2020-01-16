package com.palyrobotics.frc2020.config.subsystem;

import java.util.List;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;

public class SpinnerConfig extends SubsystemConfigBase {

	public List<Integer> colorSensorRedRGB, colorSensorYellowRGB, colorSensorCyanRGB, colorSensorGreenRGB;
	public double idleOutput, rotationOutput, positionOutput;
}
