package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.SmartGains;

public class DriveConfig extends SubsystemConfigBase {

	public Gains velocityGains;
	public SmartGains smartVelocityGains, turnGains;
	public double quickStopWeight, quickTurnScalar, quickStopDeadBand, quickStopScalar, turnSensitivity,
			lowNegativeInertiaThreshold, lowNegativeInertiaFarScalar, lowNegativeInertiaCloseScalar,
			lowNegativeInertiaTurnScalar, wheelNonLinearity, controllerRampRate;
	public int stallCurrentLimit, freeCurrentLimit, freeRpmLimit, nonlinearPasses;
	public double maxPathVelocityMetersPerSecond, maxPathAccelerationMetersPerSecondSquared;
}
