package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.util.control.Gains;
import com.palyrobotics.frc2019.util.control.SmartGains;
import com.revrobotics.ControlType;

public class SparkMaxOutput {
    private ControlType mSparkMode;
    private Gains mGains;

    private double mReference, mArbitraryDemand;

    public SparkMaxOutput() {
        this(ControlType.kDutyCycle);
    }

    public SparkMaxOutput(ControlType controlType) {
        mSparkMode = controlType;
    }

    public void setTargetSmartVelocity(double targetVelocity, double arbitraryDemand, SmartGains gains) {
        mSparkMode = ControlType.kSmartVelocity;
        mReference = targetVelocity;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setTargetVelocity(double targetVelocity, Gains gains) {
        setTargetVelocity(targetVelocity, 0.0, gains);
    }

    public void setTargetVelocity(double targetVelocity, double arbitraryDemand, Gains gains) {
        mSparkMode = ControlType.kVelocity;
        mReference = targetVelocity;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setTargetPosition(double positionSetPoint, Gains gains) {
        setTargetPosition(positionSetPoint, 0.0, gains);
    }

    public void setTargetPosition(double positionSetPoint, double arbitraryDemand, Gains gains) {
        mSparkMode = ControlType.kPosition;
        mReference = positionSetPoint;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setTargetPositionSmartMotion(double positionSetPoint, SmartGains gains) {
        setTargetPositionSmartMotion(positionSetPoint, 0.0, gains);
    }

    public void setTargetPositionSmartMotion(double positionSetPoint, double arbitraryDemand, SmartGains gains) {
        mSparkMode = ControlType.kSmartMotion;
        mReference = positionSetPoint;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setIdle() {
        setPercentOutput(0.0);
    }

    public void setPercentOutput(double output) {
        mSparkMode = ControlType.kDutyCycle;
        mReference = output;
        mArbitraryDemand = 0.0;
        mGains = null;
    }

    public void setVoltage(double output) {
        mSparkMode = ControlType.kVoltage;
        mReference = output;
        mArbitraryDemand = 0.0;
        mGains = null;
    }

    public Gains getGains() {
        return mGains;
    }

    public double getReference() {
        return mReference;
    }

    public double getArbitraryDemand() {
        return mArbitraryDemand;
    }

    public ControlType getControlType() {
        return mSparkMode;
    }
}
