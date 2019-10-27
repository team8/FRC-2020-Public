package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.dashboard.LiveGraph;
import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;

/**
 * Implements constant curvature driving. Yoinked from 254 code
 */
public class CheesyDriveHelper {
    private double
            mOldWheel, mPreviousWheelForRamp, mPreviousThrottleForRamp,
            mQuickStopAccumulator, mNegativeInertiaAccumulator,
            mBrownOutTimeSeconds;
    private final DriveConfig driveConfig = Configs.get(DriveConfig.class);
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    public SparkDriveSignal cheesyDrive(Commands commands, RobotState robotState) {
        double totalPowerMultiplier;
        if (RobotController.isBrownedOut()) {
            totalPowerMultiplier = driveConfig.brownOutInitialNerfMultiplier;
            mBrownOutTimeSeconds = Timer.getFPGATimestamp();
        } else {
            totalPowerMultiplier = MathUtil.clamp(
                    driveConfig.brownOutInitialNerfMultiplier
                            + (Timer.getFPGATimestamp() - mBrownOutTimeSeconds)
                            * (1 / driveConfig.brownOutRecoverySeconds) * (1 - driveConfig.brownOutInitialNerfMultiplier),
                    driveConfig.brownOutInitialNerfMultiplier, 1.0
            );
        }

        CSVWriter.addData("drivePowerMultiplier", totalPowerMultiplier);
        LiveGraph.getInstance().add("drivePowerMultiplier", totalPowerMultiplier);

        double throttle = commands.driveThrottle, wheel = commands.driveWheel;

        double absoluteThrottle = Math.abs(throttle);
        if (absoluteThrottle > driveConfig.throttleAccelerationThreshold && absoluteThrottle > Math.abs(mPreviousThrottleForRamp)) {
            throttle = mPreviousThrottleForRamp + Math.signum(throttle) * driveConfig.throttleAccelerationLimit;
            absoluteThrottle = Math.abs(throttle);
        }
        mPreviousThrottleForRamp = throttle;

        double absoluteWheel = Math.abs(wheel);
        if (absoluteWheel > driveConfig.wheelAccelerationThreshold && absoluteWheel > Math.abs(mPreviousWheelForRamp)) {
            wheel = mPreviousWheelForRamp + Math.signum(wheel) * driveConfig.wheelAccelerationLimit;
            absoluteWheel = Math.abs(wheel);
        }
        mPreviousWheelForRamp = wheel;

        // Quick-turn if right trigger is pressed
        boolean isQuickTurn = robotState.isQuickTurning = commands.isQuickTurn;

        wheel = MathUtil.handleDeadBand(wheel, DrivetrainConstants.kDeadband);
        throttle = MathUtil.handleDeadBand(throttle, DrivetrainConstants.kDeadband);

        double negativeWheelInertia = wheel - mOldWheel;
        mOldWheel = wheel;

        // Map linear wheel input onto a sin wave, three passes
        for (int i = 0; i < driveConfig.nonlinearPasses; i++)
            wheel = applyWheelNonLinearPass(wheel, driveConfig.wheelNonLinearity);

        // Negative inertia
        double negativeInertiaScalar;
        if (wheel * negativeWheelInertia > 0) {
            // If we are moving away from zero - trying to get more wheel
            negativeInertiaScalar = driveConfig.lowNegativeInertiaTurnScalar;
        } else {
            // Going back to zero
            if (absoluteWheel > driveConfig.lowNegativeInertiaThreshold) {
                negativeInertiaScalar = driveConfig.lowNegativeInertiaFarScalar;
            } else {
                negativeInertiaScalar = driveConfig.lowNegativeInertiaCloseScalar;
            }
        }

        double negativeInertiaPower = negativeWheelInertia * negativeInertiaScalar;
        mNegativeInertiaAccumulator += negativeInertiaPower;

        wheel += mNegativeInertiaAccumulator;
        if (mNegativeInertiaAccumulator > 1.0) {
            mNegativeInertiaAccumulator -= 1.0;
        } else if (mNegativeInertiaAccumulator < -1.0) {
            mNegativeInertiaAccumulator += 1.0;
        } else {
            mNegativeInertiaAccumulator = 0.0;
        }

        // Quick-turn allows us to turn in place without having to be moving forward or backwards
        double angularPower, overPower;
        if (isQuickTurn) {
            if (absoluteThrottle < driveConfig.quickStopDeadBand) {
                double alpha = driveConfig.quickStopWeight;
                mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + alpha * MathUtil.clamp01(wheel) * driveConfig.quickStopScalar;
            }
            overPower = 1.0;
            angularPower = wheel * driveConfig.quickTurnScalar;
        } else {
            overPower = 0.0;
            angularPower = absoluteThrottle * wheel * driveConfig.turnSensitivity - mQuickStopAccumulator;
            if (mQuickStopAccumulator > 1.0) {
                mQuickStopAccumulator -= 1.0;
            } else if (mQuickStopAccumulator < -1.0) {
                mQuickStopAccumulator += 1.0;
            } else {
                mQuickStopAccumulator = 0.0;
            }
        }

        double linearPower = throttle;

        double rightPower, leftPower;
        rightPower = leftPower = linearPower;
        leftPower += angularPower;
        rightPower -= angularPower;

        if (leftPower > 1.0) {
            rightPower -= overPower * (leftPower - 1.0);
            leftPower = 1.0;
        } else if (rightPower > 1.0) {
            leftPower -= overPower * (rightPower - 1.0);
            rightPower = 1.0;
        } else if (leftPower < -1.0) {
            rightPower += overPower * (-1.0 - leftPower);
            leftPower = -1.0;
        } else if (rightPower < -1.0) {
            leftPower += overPower * (-1.0 - rightPower);
            rightPower = -1.0;
        }

        mSignal.leftOutput.setPercentOutput(leftPower * totalPowerMultiplier);
        mSignal.rightOutput.setPercentOutput(rightPower * totalPowerMultiplier);
        return mSignal;
    }

    private double applyWheelNonLinearPass(double wheel, double wheelNonLinearity) {
        return Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
    }

    public void reset() {
        mNegativeInertiaAccumulator = mQuickStopAccumulator = 0.0;
        mOldWheel = mPreviousWheelForRamp = mPreviousThrottleForRamp = 0.0;
    }
}
