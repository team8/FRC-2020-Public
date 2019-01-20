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

    public SparkMaxOutput(Gains gains, ControlType controlMode, double setpoint) {
        mGains = gains;
        mSparkMode = controlMode;
        mSparkSetpoint = setpoint;
    }

    public void setTargetVelocity(double velocitySetpoint) {
        this.mSparkSetpoint = velocitySetpoint;
        this.mSparkMode = ControlType.kVelocity;
    }

    public void setTargetPosition(double posSetpoint) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
        this.arbitraryDemand = 0.0;
    }

    public void setTargetPosition(double posSetpoint, double arbitraryDemand) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
        this.arbitraryDemand = arbitraryDemand;
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
        return this.getControlType();
    }
}
