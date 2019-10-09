package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.vision.Limelight;

/**
 * CheesyDriveHelper implements the calculations used in CheesyDrive for teleop control. Returns a DriveSignal for the motor output
 */
public class VisionDriveHelper {

    private boolean mInitialBrake;
    private double mOldThrottle = 0.0, mBrakeRate;
    private boolean found = false;
    private SynchronousPID pidController;

    private double oldYawToTarget;
    private long oldTime;

    public SparkDriveSignal visionDrive(Commands commands, RobotState robotState) {
        double throttle = -robotState.leftStickInput.getY();

        //Braking if left trigger is pressed
        boolean isBraking = robotState.leftStickInput.getTriggerPressed();

        throttle = ChezyMath.handleDeadband(throttle, DrivetrainConstants.kDeadband);

        double leftPower, rightPower;

        double angularPower;

        //linear power is what's actually sent to motor, throttle is input
        double linearPower = throttle;

        //Handle braking
        if(isBraking) {
            //Set up braking rates for linear deceleration in a set amount of time
            if(mInitialBrake) {
                mInitialBrake = false;
                //Old throttle initially set to throttle
                mOldThrottle = linearPower;
                //Braking rate set
                mBrakeRate = mOldThrottle / DrivetrainConstants.kCyclesUntilStop;
            }

            //If braking is not complete, decrease by the brake rate
            if(Math.abs(mOldThrottle) >= Math.abs(mBrakeRate)) {
                //reduce throttle
                mOldThrottle -= mBrakeRate;
                linearPower = mOldThrottle;
            } else {
                linearPower = 0;
            }
        } else {
            mInitialBrake = true;
        }

        if(Limelight.getInstance().isTargetFound()) {
            double kP = .03;
            double kD = .005;
//            double kP = Limelight.getInstance().getTargetArea();
//            double kP = 1.0/Limelight.getInstance().getCorrectedEstimatedDistanceZ();
//            double kP = .010 * Math.sqrt(Limelight.getInstance().getCorrectedEstimatedDistanceZ());
            //double kP = Limelight.getInstance().getCorrectedEstimatedDistanceZ();
            angularPower = -Limelight.getInstance().getYawToTarget() * kP
                    - ((Limelight.getInstance().getYawToTarget() - oldYawToTarget) / (System.currentTimeMillis() - oldTime) * 1000) * kD;
            oldYawToTarget = Limelight.getInstance().getYawToTarget();
            oldTime = System.currentTimeMillis();
            // |angularPower| should be at most 0.6
            if (angularPower > 0.6) angularPower = 0.75;
            if (angularPower < -0.6) angularPower = -0.75;
        } else {
            found = false;
            oldTime = System.currentTimeMillis();
            angularPower = 0;
        }

        rightPower = leftPower = mOldThrottle = linearPower;


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

        SparkMaxOutput left = new SparkMaxOutput();
        SparkMaxOutput right = new SparkMaxOutput();
        SparkDriveSignal out = new SparkDriveSignal(left, right);

        left.setPercentOutput(leftPower);
        right.setPercentOutput(rightPower);
        return out;
    }

    /**
     * Throttle tuning functions
     */
    public double remapThrottle(double initialThrottle) {
        double x = Math.abs(initialThrottle);
        switch(OtherConstants.kDriverName) {
            case BRYAN:
                //Reversal of directions
                //Stick a 0 cycle in between
                if(initialThrottle * mOldThrottle < 0) {
                    return 0.0;
                }

                //Increase in magnitude, deceleration is fine. This misses rapid direction switches, but that's up to driver
                if(x > Math.abs(mOldThrottle)) {
                    x = mOldThrottle + Math.signum(initialThrottle) * DrivetrainConstants.kMaxAccelRate;
                } else {
                    x = initialThrottle;
                }

                //				x = initialThrottle;
                break;
        }
        return x;
    }

    /**
     * Limits the given input to the given magnitude.
     */
    public double limit(double v, double limit) {
        return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
    }
}