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

    // PID Controller Reference taken from the motor object.
    private CANPIDController mPidController;

    // Encoder reference, also from the motor object
    private CANEncoder mEncoder;

    /**
     * Note:  You must pass the speed controller as a parameter.
     * @param speedController
     */
    public SparkMaxOutput(CANSparkMax speedController) {
        mGains = new Gains(0,0,0,0,0,0);
        mSparkMode = ControlType.kPosition;
        mPidController = speedController.getPIDController();
        mEncoder = speedController.getEncoder();
    }

    /**
     * Note: You must pass the speed controller as a parameter
     * @param speedController
     * @param gains
     * @param controlMode
     * @param setpoint
     */
    public SparkMaxOutput(CANSparkMax speedController, Gains gains, ControlType controlMode, double setpoint) {
        mGains = gains;
        mSparkMode = controlMode;
        mSparkSetpoint = setpoint;
        mPidController = speedController.getPIDController();
        mEncoder = speedController.getEncoder();
        updateControllerGains();
    }

    public void setTargetVelocity(double velocitySetpoint) {
        this.mSparkSetpoint = velocitySetpoint;
        this.mSparkMode = ControlType.kVelocity;
        updatePIDController();
    }

    public void setTargetPoisition(double posSetpoint) {
        this.mSparkSetpoint = posSetpoint;
        this.mSparkMode = ControlType.kPosition;
        updatePIDController();
    }

    public void setPercentOut(double output) {
        this.mSparkSetpoint = output;
        this.mSparkMode = ControlType.kVoltage;
        updatePIDController();
    }

    public void updatePIDController() {
        mPidController.setReference(this.mSparkSetpoint, this.mSparkMode);
    }

    public void updateControllerGains() {
        this.mPidController.setP(mGains.P);
        this.mPidController.setD(mGains.D);
        this.mPidController.setI(mGains.I);
        this.mPidController.setFF(mGains.F);
        this.mPidController.setIZone(mGains.izone);
    }

}
