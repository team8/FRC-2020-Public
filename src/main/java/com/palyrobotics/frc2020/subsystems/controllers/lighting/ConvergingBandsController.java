package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class ConvergingBandsController extends Lighting.LEDController {

	private Color.HSV mBandColor;
	private Color.HSV mBackgroundColor;
	private int mBandLedCount;
	private int mCurrentBandPosition;
	private double mSpeed;

	public ConvergingBandsController(int initIndex, int lastIndex, Color.HSV bandColor, Color.HSV backgroundColor, int bandLedCount, int speed) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mBandColor = bandColor;
		mBackgroundColor = backgroundColor;
		mBandLedCount = bandLedCount;
		mSpeed = speed == 0 ? 0.001 : speed;
		mTimer.reset();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (mTimer.hasPeriodPassed(1 / mSpeed)) {
			mCurrentBandPosition += 1;
		}
		for (var i = 0; i < (mLastIndex - mInitIndex) / 2 - 1; i++) {
			if ((i + mCurrentBandPosition) / mBandLedCount % 2 == 0) {
				mOutputs.setHSV(i, mBandColor.getH(), mBandColor.getS(), mBandColor.getV());
				mOutputs.setHSV(mLastIndex - mInitIndex - i - 1, mBandColor.getH(),
						mBandColor.getS(), mBandColor.getV());
			} else {
				mOutputs.setHSV(i, mBackgroundColor.getH(), mBackgroundColor.getS(),
						mBackgroundColor.getV());
			}
		}
	}
}
