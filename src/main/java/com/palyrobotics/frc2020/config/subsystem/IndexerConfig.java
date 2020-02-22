package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class IndexerConfig extends SubsystemConfigBase {
	public double sparkIndexingOutput, leftTalonIndexingOutput, rightTalonIndexingOutput, feedingOutput, reversingOutput, reverseTime, caterpillarTargetEncoderTicks, maxPercentOutputCaterpillar, caterpillarTolerance;
	public Gains masterVelocityGains, slaveVelocityGains;
	public ProfiledGains positionGainsCaterpillar;
}
