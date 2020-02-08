package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import edu.wpi.first.wpilibj.Timer;

public class ConvergingBandsController extends Lighting.LEDController {

	private Color.HSV mBandColor;
	private Color.HSV mBackgroundColor;
	private int mBandLedCount;
	private Timer mTimer = new Timer();
	private int mCurrentBandPosition;
	private double mOldTimerValue;

	public ConvergingBandsController(int initIndex, int lastIndex, Color.HSV bandColor, Color.HSV backgroundColor, int bandLedCount) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mBandColor = bandColor;
		mBackgroundColor = backgroundColor;
		mBandLedCount = bandLedCount;
		mTimer.start();
		for (var i = mInitIndex; i < mLastIndex; i++) {
			mLightingOutputs.lightingOutput
					.add(Color.HSV.getNewInstance(bandColor.getH(), bandColor.getS(), bandColor.getV()));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() * 6) != mOldTimerValue) {
			mCurrentBandPosition += 1;
		}
		for (var i = 0; i < (mLastIndex - mInitIndex) / 2 - 1; i++) {
			if ((i + mCurrentBandPosition) / mBandLedCount % 2 == 0) {
				mLightingOutputs.lightingOutput.get(i).setHSV(mBandColor.getH(), mBandColor.getS(), mBandColor.getV());
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1).setHSV(mBandColor.getH(),
						mBandColor.getS(), mBandColor.getV());
			} else {
				mLightingOutputs.lightingOutput.get(i).setHSV(mBackgroundColor.getH(), mBackgroundColor.getS(),
						mBackgroundColor.getV());
			}
		}
		mOldTimerValue = Math.round(mTimer.get() * 6);
	}
}
