package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.ProfiledGains;

@SuppressWarnings ("java:S1104")
public class DriveConfig extends SubsystemConfigBase {

	public Gains velocityGains;
	public ProfiledGains profiledVelocityGains, turnGains;
	public double turnGainsS;
	public double quickStopWeight, quickTurnScalar, quickStopDeadBand, quickStopScalar, slowTurnScalar, turnSensitivity,
			lowNegativeInertiaThreshold, lowNegativeInertiaFarScalar, lowNegativeInertiaCloseScalar,
			lowNegativeInertiaTurnScalar, wheelNonLinearity;
	public int nonlinearPasses;
	public double pathVelocityMetersPerSecond, pathAccelerationMetersPerSecondSquared;
	public double allowableYawErrorDegrees;
}
