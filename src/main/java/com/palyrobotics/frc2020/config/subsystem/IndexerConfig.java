package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class IndexerConfig extends SubsystemConfigBase {

	public double sparkIndexingOutput, leftTalonIndexingOutput, rightTalonIndexingOutput, feedingOutput, reversingOutput, reverseTime;
	public double pulsePeriod;
	public Gains masterVelocityGains, slaveVelocityGains;
}
