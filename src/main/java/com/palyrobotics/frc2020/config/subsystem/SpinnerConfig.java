package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

public class SpinnerConfig extends SubsystemConfigBase {

	public double rotSetPoint;
	public ProfiledGains profiledRotControlVelocityGains;
	public ProfiledGains profiledPosControlVelocityGains;

}
