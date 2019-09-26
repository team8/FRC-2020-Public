package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.vision.Limelight;

/**
 * CheesyDriveHelper implements the calculations used in CheesyDrive for teleop control. Returns a DriveSignal for the motor output
 */
public class VisionDriveHelper {

    private final Limelight mLimelight = Limelight.getInstance();

    private boolean mInitialBrake;
    private double mLastThrottle, mBrakeRate;
    private boolean found;
    private SynchronousPID mPidController = new SynchronousPID(0.03, 0.0, 0.005);

    private double mLastYawError;
    private long mLastTimeMs;

    private final SparkDriveSignal mSignal = SparkDriveSignal.getNeutralSignal();

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
        if (isBraking) {
            //Set up braking rates for linear deceleration in a set amount of time
            if (mInitialBrake) {
                mInitialBrake = false;
                //Old throttle initially set to throttle
                mLastThrottle = linearPower;
                //Braking rate set
                mBrakeRate = mLastThrottle / DrivetrainConstants.kCyclesUntilStop;
            }

            //If braking is not complete, decrease by the brake rate
            if (Math.abs(mLastThrottle) >= Math.abs(mBrakeRate)) {
                //reduce throttle
                mLastThrottle -= mBrakeRate;
                linearPower = mLastThrottle;
            } else {
                linearPower = 0;
            }
        } else {
            mInitialBrake = true;
        }

        if (Limelight.getInstance().isTargetFound()) {
            double kP = 0.03, kD = 0.005;
//            double kP = Limelight.getInstance().getTargetArea();
//            double kP = 1.0/Limelight.getInstance().getCorrectedEstimatedDistanceZ();
//            double kP = .010 * Math.sqrt(Limelight.getInstance().getCorrectedEstimatedDistanceZ());
            //double kP = Limelight.getInstance().getCorrectedEstimatedDistanceZ();
            long
                    currentTime = System.currentTimeMillis(),
                    deltaSeconds = (currentTime - mLastTimeMs) / 1000L;
            double
                    yawError = mLimelight.getYawToTarget(),
                    yawErrorDerivative = (yawError - mLastYawError) / deltaSeconds;
            angularPower = -yawError * kP - yawErrorDerivative * kD;
            mLastYawError = yawError;
            mLastTimeMs = currentTime;
            // |angularPower| should be at most 0.6
            if (angularPower > 0.6) angularPower = 0.75;
            if (angularPower < -0.6) angularPower = -0.75;
        } else {
            found = false;
            mLastTimeMs = System.currentTimeMillis();
            angularPower = 0.0;
        }

        rightPower = leftPower = mLastThrottle = linearPower;

        angularPower *= -1;
        //angularPower *= mOldThrottle;
        leftPower *= (1 + angularPower);
        rightPower *= (1 - angularPower);

        if (leftPower > 1.0) {
            leftPower = 1.0;
        } else if (rightPower > 1.0) {
            rightPower = 1.0;
        } else if (leftPower < -1.0) {
            leftPower = -1.0;
        } else if (rightPower < -1.0) {
            rightPower = -1.0;
        }

        mSignal.leftOutput.setPercentOutput(leftPower);
        mSignal.rightOutput.setPercentOutput(rightPower);
        return mSignal;
    }

    /**
     * Throttle tuning functions
     */
    public double remapThrottle(double initialThrottle) {
        double x = Math.abs(initialThrottle);
        switch (OtherConstants.kDriverName) {
            case BRYAN:
                //Reversal of directions
                //Stick a 0 cycle in between
                if (initialThrottle * mLastThrottle < 0) {
                    return 0.0;
                }

                //Increase in magnitude, deceleration is fine. This misses rapid direction switches, but that's up to driver
                if (x > Math.abs(mLastThrottle)) {
                    x = mLastThrottle + Math.signum(initialThrottle) * DrivetrainConstants.kMaxAccelRate;
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