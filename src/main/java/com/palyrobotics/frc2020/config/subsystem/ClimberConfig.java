package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

public class ClimberConfig extends SubsystemConfigBase {

	public double climberTopHeight;
	public double loweringPercentOutput;
	public double climbingPercentOutput;

	public double raisingArbitraryDemand;
	public ProfiledGains raisingGains;
}
