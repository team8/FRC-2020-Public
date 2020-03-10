package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class DivergingBandsController extends Lighting.LEDController {

	private Color.HSV mBandColor;
	private Color.HSV mBackgroundColor;
	private int mBandLedCount;
	private int mCurrentBandPosition;
	private double mDuration = -1;
	private long mOldModValue;

	/**
	 * Band color converges to center of strip
	 *
	 * @param startIndex      Initial index upon which led patterns should start
	 * @param lastIndex       End index upon which led patterns should stop
	 * @param bandColor       Color that should pulse through led strip
	 * @param backgroundColor Background color upon which converging effect will occur.
	 */

	public DivergingBandsController(int startIndex, int lastIndex, Color.HSV bandColor, Color.HSV backgroundColor, int bandLedCount, double speed) {
		super(startIndex, lastIndex);
		mBandColor = bandColor;
		mBackgroundColor = backgroundColor;
		mBandLedCount = bandLedCount;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mPriority = 1;
		mTimer.start();
	}

	public DivergingBandsController(int startIndex, int lastIndex, Color.HSV bandColor, Color.HSV backgroundColor, int bandLedCount, double speed, int duration) {
		super(startIndex, lastIndex);
		mBandColor = bandColor;
		mBackgroundColor = backgroundColor;
		mBandLedCount = bandLedCount;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mDuration = duration;
		mPriority = 1;
		mTimer.start();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 != mOldModValue) {
			mOldModValue = Math.round(mTimer.get() / mSpeed) % 2;
			mCurrentBandPosition += 1;
		}
		for (var i = 0; i < (mLastIndex - mStartIndex) / 2 + 1; i++) {
			if ((i + mCurrentBandPosition) / mBandLedCount % 2 == 0) {
				mOutputs.lightingOutput.get(i).setHSV(mBandColor.getH(), mBandColor.getS(), mBandColor.getV());
				mOutputs.lightingOutput.get(mLastIndex - mStartIndex - i).setHSV(mBandColor.getH(),
						mBandColor.getS(), mBandColor.getV());
			} else {
				mOutputs.lightingOutput.get(i).setHSV(mBackgroundColor.getH(), mBackgroundColor.getS(),
						mBackgroundColor.getV());
				mOutputs.lightingOutput.get(mLastIndex - mStartIndex - i).setHSV(mBackgroundColor.getH(),
						mBackgroundColor.getS(), mBackgroundColor.getV());
			}
		}
	}

	@Override
	public boolean checkFinished() {
		return mDuration != -1 && mTimer.hasElapsed(mDuration);
	}
}
