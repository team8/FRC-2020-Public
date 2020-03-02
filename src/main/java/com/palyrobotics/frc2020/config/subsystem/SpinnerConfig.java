package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class SpinnerConfig extends SubsystemConfigBase {

	public double rotationControlPercentOutput, positionControlPercentOutput;
	public int rotationControlColorChangeRequirementCount;
}
