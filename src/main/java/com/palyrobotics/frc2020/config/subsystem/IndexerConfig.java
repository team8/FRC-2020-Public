package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.SmartGains;

public class IndexerConfig extends SubsystemConfigBase {

	public double horizontalIntakeVelocity, verticalIntakeVelocity;

	public SmartGains horizontalProfiledVelocityGains, verticalProfiledVelocityGains;
}
