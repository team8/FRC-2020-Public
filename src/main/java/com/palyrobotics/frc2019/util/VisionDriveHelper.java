package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.VisionConfig;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.vision.Limelight;

/**
 * CheesyDriveHelper implements the calculations used in CheesyDrive for teleop control. Returns a DriveSignal for the motor output
 */
public class VisionDriveHelper {

    private static final double kMaxAngularPower = 0.75;

    private final Limelight mLimelight = Limelight.getInstance();
    private final VisionConfig mConfig = Configs.get(VisionConfig.class);

    private final SparkDriveSignal mSignal = new SparkDriveSignal();

    private boolean mInitialBrake;
    private double mLastThrottle, mBrakeRate;
//    private double mLastYawError;
//    private long mLastTimeMs;
    private SynchronousPID mPidController = new SynchronousPID(mConfig.p, mConfig.i, mConfig.d);

    public SparkDriveSignal visionDrive(Commands commands, RobotState robotState) {

        double throttle = -robotState.leftStickInput.getY();

        // Braking if left trigger is pressed
        boolean isBraking = robotState.leftStickInput.getTriggerPressed();

        throttle = ChezyMath.handleDeadband(throttle, DrivetrainConstants.kDeadband);

        double leftOutput, rightOutput;

        double angularPower;

        // linear power is what's actually sent to motor, throttle is input
        double linearPower = throttle;

        // Handle braking
        if (isBraking) {
            // Set up braking rates for linear deceleration in a set amount of time
            if (mInitialBrake) {
                mInitialBrake = false;
                // Old throttle initially set to throttle
                mLastThrottle = linearPower;
                // Braking rate set
                mBrakeRate = mLastThrottle / DrivetrainConstants.kCyclesUntilStop;
            }

            // If braking is not complete, decrease by the brake rate
            if (Math.abs(mLastThrottle) >= Math.abs(mBrakeRate)) {
                // reduce throttle
                mLastThrottle -= mBrakeRate;
                linearPower = mLastThrottle;
            } else {
                linearPower = 0;
            }
        } else {
            mInitialBrake = true;
        }

        if (Limelight.getInstance().isTargetFound()) {
//            double kP = 0.03, kD = 0.005;
//            double kP = Limelight.getInstance().getTargetArea();
//            double kP = 1.0/Limelight.getInstance().getCorrectedEstimatedDistanceZ();
//            double kP = .010 * Math.sqrt(Limelight.getInstance().getCorrectedEstimatedDistanceZ());
//            double kP = Limelight.getInstance().getCorrectedEstimatedDistanceZ();
//            long
//                    currentTime = System.currentTimeMillis(),
//                    deltaSeconds = (currentTime - mLastTimeMs) / 1000L;
//            double
//                    yawError = mLimelight.getYawToTarget(),
//                    yawErrorDerivative = (yawError - mLastYawError) / deltaSeconds;
//            angularPower = -yawError * kP - yawErrorDerivative * kD;
//            mLastYawError = yawError;
//            mLastTimeMs = currentTime;
            angularPower = mPidController.calculate(mLimelight.getYawToTarget());

            // |angularPower| should be at most 0.6
            if (angularPower > kMaxAngularPower) angularPower = kMaxAngularPower;
            if (angularPower < -kMaxAngularPower) angularPower = -kMaxAngularPower;
        } else {
//            mLastTimeMs = System.currentTimeMillis();
            angularPower = 0.0;
        }

        rightOutput = leftOutput = mLastThrottle = linearPower;

        angularPower *= -1.0;
//        angularPower *= mOldThrottle;
        leftOutput *= (1 + angularPower);
        rightOutput *= (1 - angularPower);

        if (leftOutput > 1.0) {
            leftOutput = 1.0;
        } else if (rightOutput > 1.0) {
            rightOutput = 1.0;
        } else if (leftOutput < -1.0) {
            leftOutput = -1.0;
        } else if (rightOutput < -1.0) {
            rightOutput = -1.0;
        }

        mSignal.leftOutput.setPercentOutput(leftOutput);
        mSignal.rightOutput.setPercentOutput(rightOutput);
        return mSignal;
    }

//    /**
//     * Throttle tuning functions
//     */
//    public double remapThrottle(double initialThrottle) {
//        double x = Math.abs(initialThrottle);
//        switch (OtherConstants.kDriverName) {
//            case BRYAN:
//                //Reversal of directions
//                //Stick a 0 cycle in between
//                if (initialThrottle * mLastThrottle < 0) {
//                    return 0.0;
//                }
//
//                //Increase in magnitude, deceleration is fine. This misses rapid direction switches, but that's up to driver
//                if (x > Math.abs(mLastThrottle)) {
//                    x = mLastThrottle + Math.signum(initialThrottle) * DrivetrainConstants.kMaxAccelRate;
//                } else {
//                    x = initialThrottle;
//                }
//
//                //				x = initialThrottle;
//                break;
//        }
//        return x;
//    }
//
//    /**
//     * Limits the given input to the given magnitude.
//     */
//    public double limit(double v, double limit) {
//        return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
//    }
}