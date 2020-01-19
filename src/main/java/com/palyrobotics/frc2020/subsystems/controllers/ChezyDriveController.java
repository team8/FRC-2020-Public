package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.MathUtil;

/**
 * Implements constant curvature driving. Yoinked from 254 code
 */
public class ChezyDriveController extends Drive.DriveController {

	private double mLastWheel, mQuickStopAccumulator, mNegativeInertiaAccumulator;

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		// Quick-turn if right trigger is pressed
		boolean isQuickTurn = robotState.driveIsQuickTurning = commands.getDriveWantsQuickTurn();

		double throttle = commands.getDriveWantedThrottle(), wheel = commands.getDriveWantedWheel();

		double absoluteThrottle = Math.abs(throttle), absoluteWheel = Math.abs(wheel);

		wheel = MathUtil.handleDeadBand(wheel, DrivetrainConstants.kDeadBand);
		throttle = MathUtil.handleDeadBand(throttle, DrivetrainConstants.kDeadBand);

		double negativeWheelInertia = wheel - mLastWheel;
		mLastWheel = wheel;

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

		// Quick-turn allows us to turn in place without having to be moving forward or
		// backwards
		double angularPower, overPower;
		if (isQuickTurn) {
			if (absoluteThrottle < mDriveConfig.quickStopDeadBand) {
				double alpha = mDriveConfig.quickStopWeight;
				mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator
						+ alpha * MathUtil.clamp01(wheel) * mDriveConfig.quickStopScalar;
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

		mDriveOutputs.leftOutput.setPercentOutput(leftPower);
		mDriveOutputs.rightOutput.setPercentOutput(rightPower);
	}

	private double applyWheelNonLinearPass(double wheel, double wheelNonLinearity) {
		return Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
	}
}
