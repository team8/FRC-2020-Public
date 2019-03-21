package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.ChezyMath;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.SynchronousPID;
import com.palyrobotics.frc2019.vision.Limelight;

/**
 * Turns drivetrain using the gyroscope and bang-bang control loop
 *
 * @author Robbie, Nihar
 *
 */
public class VisionClosedController implements Drive.DriveController {

    private double oldYawToTarget;
    private long oldTime;

    private final double distancePowConst = Gains.kVidarTrajectorykV;

    /**
     *            Pass in the latest robot state
     * @param
     */
    public VisionClosedController() {
    }

    public double getAdjustedDistancePower() {
        if (Limelight.getInstance().isTargetFound()) {
            return Limelight.getInstance().getCorrectedEstimatedDistanceZ() * this.distancePowConst;
        }
        else {
            return 0.0;
        }
    }

    @Override
    public SparkSignal update(RobotState robotState) {
        double angularPower;
        if(Limelight.getInstance().isTargetFound()) {
            double kP = .03;
            double kD = .005;

            angularPower = -Limelight.getInstance().getYawToTarget() * kP
                    - ((Limelight.getInstance().getYawToTarget() - oldYawToTarget) / (System.currentTimeMillis() - oldTime) * 1000) * kD;
            oldYawToTarget = Limelight.getInstance().getYawToTarget();
            oldTime = System.currentTimeMillis();
            // |angularPower| should be at most 0.6
            if (angularPower > 0.6) angularPower = 0.6;
            if (angularPower < -0.6) angularPower = -0.6;
        } else {
            return SparkSignal.getNeutralSignal();
        }

        double leftPower =  getAdjustedDistancePower();
        double rightPower = getAdjustedDistancePower();

        angularPower *= -1;
        //angularPower *= mOldThrottle;
        leftPower *= (1 + angularPower);
        rightPower *= (1 - angularPower);


        if(leftPower > 1.0) {
            leftPower = 1.0;
        } else if(rightPower > 1.0) {
            rightPower = 1.0;
        } else if(leftPower < -1.0) {
            leftPower = -1.0;
        } else if(rightPower < -1.0) {
            rightPower = -1.0;
        }

        SparkSignal mSignal = SparkSignal.getNeutralSignal();

        mSignal.leftMotor.setPercentOutput(leftPower);
        mSignal.rightMotor.setPercentOutput(rightPower);
        return mSignal;
    }

    @Override
    public Pose getSetpoint() {
        Pose setpoint = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
        return setpoint;
    }

    @Override
    public boolean onTarget() {
        // once the target is out of sight, we are on target
        return !Limelight.getInstance().isTargetFound();
    }

}