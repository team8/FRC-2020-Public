package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class ClimberConfig extends SubsystemConfigBase {

	public double climberTopHeight;
	public double loweringPercentOutput;
	public double allowablePositionError;
	public double velocityChangeThreshold;

	public double raisingArbitraryDemand, climbingArbitraryDemand;
	public double positionConversionFactor, velocityConversionFactor;
	public ProfiledGains raisingGains, climbingGains;
}
