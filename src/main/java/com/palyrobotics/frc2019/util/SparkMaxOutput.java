package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Gains;
import com.revrobotics.ControlType;

public class SparkMaxOutput {

    // Control Gains
    private Gains mGains = Gains.emptyGains;

    private ControlType mSparkMode;

    // Output Reference
    private double mSparkReference;

    private double mArbitraryDemand;

    public SparkMaxOutput() {
        this(ControlType.kPosition);
    } // TODO probably should change to duty cycle

    public SparkMaxOutput(ControlType controlType) {
        mSparkMode = controlType;
    }

    public SparkMaxOutput(SparkMaxOutput otherSpark) {
        mGains = otherSpark.mGains;
        mSparkMode = otherSpark.mSparkMode;
        mSparkReference = otherSpark.mSparkReference;
        mArbitraryDemand = otherSpark.mArbitraryDemand;
    }

    public SparkMaxOutput(Gains gains, ControlType controlMode, double reference) {
        mGains = gains;
        mSparkMode = controlMode;
        mSparkReference = reference;
    }

    public static SparkMaxOutput getIdle() {
        return new SparkMaxOutput(ControlType.kDutyCycle);
    }

    public void setTargetSmartVelocity(double targetVelocity, double arbitraryDemand) {
        mSparkReference = targetVelocity;
        mSparkMode = ControlType.kSmartVelocity;
        mArbitraryDemand = arbitraryDemand;
    }

    public void setTargetVelocity(double targetVelocity) {
        mSparkReference = targetVelocity;
        mSparkMode = ControlType.kVelocity;
    }

    public void setTargetVelocity(double targetVelocity, Gains gains) {
        setTargetVelocity(targetVelocity);
        mGains = gains;
    }

    public void setTargetVelocity(double targetVelocity, double arbitraryDemand, Gains gains) {
        setTargetVelocity(targetVelocity, gains);
        mArbitraryDemand = arbitraryDemand;
    }

    public void setTargetPosition(double positionSetPoint, Gains gains) {
        setTargetPosition(positionSetPoint, 0.0, gains);
    }

    public void setTargetPosition(double positionSetPoint, double arbitraryDemand, Gains gains) {
        mSparkReference = positionSetPoint;
        mSparkMode = ControlType.kPosition;
        mArbitraryDemand = arbitraryDemand;
        mGains = gains;
    }

    public void setTargetPositionSmartMotion(double positionSetPoint) {
        setTargetPositionSmartMotion(positionSetPoint, 0.0);
    }

    public void setTargetPositionSmartMotion(double positionSetPoint, double arbitraryDemand) {
        mSparkReference = positionSetPoint;
        mSparkMode = ControlType.kSmartMotion;
        mArbitraryDemand = arbitraryDemand;
    }

    public void setIdle() {
        setPercentOutput(0.0);
    }

    public void setPercentOutput(double output) {
        mSparkReference = output;
        mSparkMode = ControlType.kDutyCycle;
        mArbitraryDemand = 0.0;
    }

    public void setVoltage(double output) {
        mSparkReference = output;
        mSparkMode = ControlType.kVoltage;
        mArbitraryDemand = 0.0;
    }

    public void setGains(Gains gains) {
        mGains = gains;
    }

    public Gains getGains() {
        return mGains;
    }

    public double getReference() {
        return mSparkReference;
    }

    public double getArbitraryDemand() {
        return mArbitraryDemand;
    }

    public ControlType getControlType() {
        return mSparkMode;
    }
}
