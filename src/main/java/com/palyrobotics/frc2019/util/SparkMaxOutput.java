package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Gains;
import com.revrobotics.ControlType;

public class SparkMaxOutput {

    // Control Gains
    private Gains mGains;

    // Control Type
    private ControlType mSparkMode;

    // Output Setpoint
    private double mSparkReference;

    // Arbitrary Feed Forward / Demand
    private double mArbitraryDemand;

    private double mSmartMotionPositionConversion;

    public SparkMaxOutput() {
        this(ControlType.kPosition);
    }

    public SparkMaxOutput(ControlType controlType) {
        mGains = new Gains(0, 0, 0, 0, 0, 0);
        mSparkMode = controlType;
    }

    public SparkMaxOutput(SparkMaxOutput otherSpark) {
        mGains = otherSpark.mGains;
        mArbitraryDemand = otherSpark.mArbitraryDemand;
        mSparkReference = otherSpark.mSparkReference;
        mSparkMode = otherSpark.mSparkMode;
    }

    public SparkMaxOutput(Gains gains, ControlType controlMode, double setpoint) {
        mGains = gains;
        mSparkMode = controlMode;
        mSparkReference = setpoint;
    }

    public void setTargetVelocity(double velocitySetpoint) {
        mSparkReference = velocitySetpoint;
        mSparkMode = ControlType.kVelocity;
    }

    public void setTargetVelocity(double velocitySetpoint, Gains gains) {
        mSparkReference = velocitySetpoint;
        mSparkMode = ControlType.kVelocity;
        mGains = gains;
    }

    public void setTargetVelocity(double velocitySetpoint, double arbitraryDemand, Gains gains) {
        mSparkReference = velocitySetpoint;
        mSparkMode = ControlType.kVelocity;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setTargetPosition(double posSetpoint) {
        mSparkReference = posSetpoint;
        mSparkMode = ControlType.kPosition;
        mArbitraryDemand = 0.0;
    }

    public void setTargetPosition(double posSetpoint, Gains gains) {
        mSparkReference = posSetpoint;
        mSparkMode = ControlType.kPosition;
        mArbitraryDemand = 0.0;
        mGains = gains;
    }

    public void setTargetPosition(double posSetpoint, double arbitraryDemand, Gains gains) {
        mSparkReference = posSetpoint;
        mSparkMode = ControlType.kPosition;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setTargetPositionSmartMotion(double setpoint, double positionConversion, double arbitraryDemand) {
        mSparkReference = setpoint;
        mSmartMotionPositionConversion = positionConversion;
        mSparkMode = ControlType.kSmartMotion;
        mArbitraryDemand = arbitraryDemand;
    }

    public void setPercentOutput(double output) {
        mSparkReference = output;
        mSparkMode = ControlType.kDutyCycle;
    }

    public void setVoltage(double output) {
        mSparkReference = output;
        mSparkMode = ControlType.kVoltage;
    }

    public void setGains(Gains gains) {
        mGains = gains;
    }

    public Gains getGains() {
        return mGains;
    }

    public double getSetpoint() {
        return mSparkReference;
    }

    public double getArbitraryDemand() {
        return mArbitraryDemand;
    }

    public double getSmartMotionSetpointAdjusted() {
        return mSparkReference / mSmartMotionPositionConversion;
    }

    public ControlType getControlType() {
        return mSparkMode;
    }
}
