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

	public ConvergingBandsController(int initIndex, int lastIndex, Color.HSV bandColor, Color.HSV backgroundColor, int bandLedCount, double speed) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mBandColor = bandColor;
		mBackgroundColor = backgroundColor;
		mBandLedCount = bandLedCount;
		mSpeed = speed == 0 ? 0.001 : speed;
		mTimer.start();
		for (var i = mInitIndex; i < mLastIndex; i++) {
			mOutputs.lightingOutput
					.add(new Color.HSV(bandColor.getH(), bandColor.getS(), bandColor.getV()));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get()/mSpeed) % 2 == 1) {
			mCurrentBandPosition += 1;
			mTimer.reset();
		}
		for (var i = 0; i < (mLastIndex - mInitIndex) / 2 - 1; i++) {
			if ((i + mCurrentBandPosition) / mBandLedCount % 2 == 0) {
				mOutputs.lightingOutput.get(i).setHSV(mBandColor.getH(), mBandColor.getS(), mBandColor.getV());
				mOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1).setHSV(mBandColor.getH(),
						mBandColor.getS(), mBandColor.getV());
			} else {
				mOutputs.lightingOutput.get(i).setHSV(mBackgroundColor.getH(), mBackgroundColor.getS(),
						mBackgroundColor.getV());
				mOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1).setHSV(mBackgroundColor.getH(),
						mBackgroundColor.getS(), mBackgroundColor.getV());
			}
		}
	}
}
