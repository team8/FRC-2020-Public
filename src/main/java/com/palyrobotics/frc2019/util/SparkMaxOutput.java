package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Gains;
import com.revrobotics.ControlType;

public class SparkMaxOutput {

    // Control Gains
    private Gains mGains;

    // Control Type
    private ControlType mSparkMode;

    // Output Setpoint
    private double mSparkSetpoint;

    // Arbitrary FeedForwards
    private double arbitraryDemand;

    public SparkMaxOutput() {
        mGains = new Gains(0,0,0,0,0,0);
        mSparkMode = ControlType.kPosition;
    }

    public SparkMaxOutput(SparkMaxOutput otherSpark) {
        mGains = otherSpark.getGains();
        arbitraryDemand = otherSpark.getArbitraryFF();
        mSparkSetpoint = otherSpark.getSetpoint();
        mSparkMode = otherSpark.getControlType();
    }

    public SparkMaxOutput(Gains gains, ControlType controlMode, double setpoint) {
        mGains = gains;
        mSparkMode = controlMode;
        mSparkSetpoint = setpoint;
    }

    public void setTargetVelocity(double velocitySetpoint) {
        this.mSparkSetpoint = velocitySetpoint;
        this.mSparkMode = ControlType.kVelocity;
    }

    public void setTargetVelocity(double velocitySetpoint, Gains gains) {
        this.mSparkSetpoint = velocitySetpoint;
        this.mSparkMode = ControlType.kVelocity;
        this.mGains = gains;
    }

    public void setTargetPosition(double posSetpoint) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
        this.arbitraryDemand = 0.0;
    }

    public void setTargetPosition(double posSetpoint, Gains gains) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
        this.arbitraryDemand = 0.0;
        this.mGains = gains;
    }

    public void setTargetPosition(double posSetpoint, double arbitraryDemand, Gains gains) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
        this.arbitraryDemand = arbitraryDemand;
        this.mGains = gains;
    }

    public void setPercentOutput(double output) {
        this.mSparkSetpoint = output;
        this.mSparkMode = ControlType.kDutyCycle;
    }

    public void setVoltage(double output) {
        this.mSparkSetpoint = output;
        this.mSparkMode = ControlType.kVoltage;
    }

    public void setGains(Gains gains) {
        this.mGains = gains;
    }

    public Gains getGains() {
        return this.mGains;
    }

    public double getSetpoint() {
        return this.mSparkSetpoint;
    }

    public double getArbitraryFF() {
        return this.arbitraryDemand;
    }

    public ControlType getControlType() {
        return this.mSparkMode;
    }
}
