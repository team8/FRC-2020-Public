package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FadeInFadeOutController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private double mDuration = -1;

	/**
	 * Color flashes with given delay
	 *
	 * @param startIndex   Initial index upon which led patterns should start
	 * @param lastIndex    End index upon which led patterns should stop
	 * @param flashedColor Color to be flashed on white background
	 */

	public FadeInFadeOutController(int startIndex, int lastIndex, Color.HSV flashedColor, double delay) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mSpeed = delay == 0 ? kZeroSpeed : delay;
		mTimer.start();
	}

	public FadeInFadeOutController(int startIndex, int lastIndex, Color.HSV flashedColor, double delay, double duration) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mSpeed = delay == 0 ? kZeroSpeed : delay;
		mDuration = duration;
		mTimer.start();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {

		if ((mTimer.get() % mSpeed) < (mSpeed / 2)) {

			double n = ((mTimer.get() % mSpeed) / mSpeed);

			for (int i = mStartIndex; i < mLastIndex; i++) {

				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(mFlashedColor.getH(),
						mFlashedColor.getS(),
						(int) (mFlashedColor.getV() * n));
			}
		} else {
			double n = 1 - ((mTimer.get() % mSpeed) / mSpeed);

			for (int i = mStartIndex; i < mLastIndex; i++) {

				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(mFlashedColor.getH(),
						mFlashedColor.getS(),
						(int) (mFlashedColor.getV() * n));
			}
		}

	}

	@Override
	public boolean checkFinished() {
		return mDuration != -1 && mTimer.hasElapsed(mDuration);
	}
}
