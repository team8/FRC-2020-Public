package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Gains;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

public class SparkMaxOutput {

    // Control Gains
    private Gains mGains;

    // Control Type
    private ControlType mSparkMode;

    // Output Setpoint
    private double mSparkSetpoint;

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

    public void setTargetPoisition(double posSetpoint) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
    }

    public void setPercentOut(double output) {
        this.mSparkSetpoint = output;
        this.mSparkMode = ControlType.kDutyCycle;
    }

    public void setVoltage(double output) {
        this.mSparkSetpoint = output;
        this.mSparkMode = ControlType.kVoltage;
    }

    public Gains getGains() {
        return this.mGains;
    }

    public double getSetpoint() {
        return this.mSparkSetpoint;
    }

}
