package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

public class IndexerConfig extends SubsystemConfigBase {

	public double horizontalIntakeVelocity, verticalIntakeVelocity;

	public ProfiledGains horizontalProfiledVelocityGains, verticalProfiledVelocityGains;
}
