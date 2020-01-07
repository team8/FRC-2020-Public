package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.AbstractSubsystemConfig;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.SmartGains;
import com.palyrobotics.frc2020.util.control.TrajectoryGains;

public class DriveConfig extends AbstractSubsystemConfig {

    // On-board velocity-based follower
    // kV = (gear ratio) / (pi * free speed * wheel diameter)
    // kA = (wheel radius * robot mass) / (total number of motors * gear reduction * motor stall torque)
    // kV ~ 1.1 times theoretical, kA ~ 1.4 times theoretical, kS ~ 1.3V = .11
    // presentation has a typo for kA, should be wheel radius because T = Fr

    public TrajectoryGains trajectoryGains;

    public Gains cascadingTurnGains, velocityGains;
    public SmartGains smartVelocityGains;

    public double
            quickStopWeight, quickTurnScalar, quickStopDeadBand, quickStopScalar,
            turnSensitivity,
            lowNegativeInertiaThreshold, lowNegativeInertiaFarScalar, lowNegativeInertiaCloseScalar, lowNegativeInertiaTurnScalar,
            wheelNonLinearity,
            controllerRampRate;
    public int
            stallCurrentLimit, freeCurrentLimit, freeRpmLimit,
            nonlinearPasses;
    public double maxPathVelocityMetersPerSecond, maxPathAccelerationMetersPerSecondSquared;
}
