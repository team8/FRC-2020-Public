package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.SmartGains;

public class IntakeConfig extends SubsystemConfigBase {

	public double intakingVelocity;

	public SmartGains profiledVelocityGains;
}
