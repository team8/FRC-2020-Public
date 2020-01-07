package com.palyrobotics.frc2020.util;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.util.config.Configs;

/**
 * Implements constant curvature driving. Yoinked from 254 code
 */
public class CheesyDriveHelper {

    private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
    private double
            mOldWheel,
            mQuickStopAccumulator, mNegativeInertiaAccumulator;
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    public SparkDriveSignal cheesyDrive(Commands commands, RobotState robotState) {

        // Quick-turn if right trigger is pressed
        boolean isQuickTurn = robotState.isQuickTurning = commands.isQuickTurn;

        double throttle = commands.driveThrottle, wheel = commands.driveWheel;

        double absoluteThrottle = Math.abs(throttle), absoluteWheel = Math.abs(wheel);

        wheel = MathUtil.handleDeadBand(wheel, DrivetrainConstants.kDeadBand);
        throttle = MathUtil.handleDeadBand(throttle, DrivetrainConstants.kDeadBand);

        double negativeWheelInertia = wheel - mOldWheel;
        mOldWheel = wheel;

        // Map linear wheel input onto a sin wave, three passes
        for (int i = 0; i < mDriveConfig.nonlinearPasses; i++)
            wheel = applyWheelNonLinearPass(wheel, mDriveConfig.wheelNonLinearity);

        // Negative inertia
        double negativeInertiaScalar;
        if (wheel * negativeWheelInertia > 0) {
            // If we are moving away from zero - trying to get more wheel
            negativeInertiaScalar = mDriveConfig.lowNegativeInertiaTurnScalar;
        } else {
            // Going back to zero
            if (absoluteWheel > mDriveConfig.lowNegativeInertiaThreshold) {
                negativeInertiaScalar = mDriveConfig.lowNegativeInertiaFarScalar;
            } else {
                negativeInertiaScalar = mDriveConfig.lowNegativeInertiaCloseScalar;
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
            if (absoluteThrottle < mDriveConfig.quickStopDeadBand) {
                double alpha = mDriveConfig.quickStopWeight;
                mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + alpha * MathUtil.clamp01(wheel) * mDriveConfig.quickStopScalar;
            }
            overPower = 1.0;
            angularPower = wheel * mDriveConfig.quickTurnScalar;
        } else {
            overPower = 0.0;
            angularPower = absoluteThrottle * wheel * mDriveConfig.turnSensitivity - mQuickStopAccumulator;
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

        mSignal.leftOutput.setPercentOutput(leftPower);
        mSignal.rightOutput.setPercentOutput(rightPower);
        return mSignal;
    }

    private double applyWheelNonLinearPass(double wheel, double wheelNonLinearity) {
        return Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
    }

    public void reset() {
        mNegativeInertiaAccumulator = mQuickStopAccumulator = 0.0;
        mOldWheel = 0.0;
    }
}
