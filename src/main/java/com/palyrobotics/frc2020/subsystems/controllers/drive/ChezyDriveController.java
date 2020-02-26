package com.palyrobotics.frc2020.subsystems.controllers.drive;

import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.Util;

/**
 * Implements constant curvature driving. Yoinked from 254 code
 */
public class ChezyDriveController extends Drive.DriveController {

	private double mLastWheel, mQuickStopAccumulator, mNegativeInertiaAccumulator;

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		// Quick-turn if right trigger is pressed
		boolean isQuickTurn = state.driveIsQuickTurning = commands.getDriveWantsQuickTurn();
		boolean isSlowTurn = state.driveIsSlowTurning = commands.getDriveWantsSlowTurn();
		boolean slowTurnLeft = commands.getDriveWantedSlowTurnLeft();

		double throttle = commands.getDriveWantedThrottle(), wheel = commands.getDriveWantedWheel();

		double absoluteThrottle = Math.abs(throttle), absoluteWheel = Math.abs(wheel);

		wheel = Util.handleDeadBand(wheel, DriveConstants.kDeadBand);
		throttle = Util.handleDeadBand(throttle, DriveConstants.kDeadBand);

		double negativeWheelInertia = wheel - mLastWheel;
		mLastWheel = wheel;

		// Map linear wheel input onto a sin wave, three passes
		for (int i = 0; i < mConfig.nonlinearPasses; i++) wheel = applyWheelNonLinearPass(wheel,
				mConfig.wheelNonLinearity);

		// Negative inertia
		double negativeInertiaScalar;
		if (wheel * negativeWheelInertia > 0) {
			// If we are moving away from zero - trying to get more wheel
			negativeInertiaScalar = mConfig.lowNegativeInertiaTurnScalar;
		} else {
			// Going back to zero
			if (absoluteWheel > mConfig.lowNegativeInertiaThreshold) {
				negativeInertiaScalar = mConfig.lowNegativeInertiaFarScalar;
			} else {
				negativeInertiaScalar = mConfig.lowNegativeInertiaCloseScalar;
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
			if (absoluteThrottle < mConfig.quickStopDeadBand) {
				double alpha = mConfig.quickStopWeight;
				mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator +
						alpha * Util.clamp01(wheel) * mConfig.quickStopScalar;
			}
			overPower = 1.0;
			angularPower = wheel * mConfig.quickTurnScalar;
		} else if (isSlowTurn) {
			overPower = 1.0;
			angularPower = (slowTurnLeft) ? -mConfig.slowTurnScalar : mConfig.slowTurnScalar;
		} else {
			overPower = 0.0;
			angularPower = absoluteThrottle * wheel * mConfig.turnSensitivity - mQuickStopAccumulator;
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

		mOutputs.leftOutput.setPercentOutput(leftPower);
		mOutputs.rightOutput.setPercentOutput(rightPower);
	}

	private double applyWheelNonLinearPass(double wheel, double wheelNonLinearity) {
		return Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
	}
}
