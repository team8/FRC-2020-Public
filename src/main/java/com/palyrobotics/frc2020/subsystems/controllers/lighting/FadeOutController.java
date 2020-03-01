package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FadeOutController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private double mDuration = -1;

	/**
	 * Color flashes with given delay
	 *
	 * @param startIndex   Initial index upon which led patterns should start
	 * @param lastIndex    End index upon which led patterns should stop
	 * @param flashedColor Color to be flashed on white background
	 */

	public FadeOutController(int startIndex, int lastIndex, boolean noDestroy, Color.HSV flashedColor, int delay) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mSpeed = delay == 0 ? kZeroSpeed : delay;
		kPriority = 1;
		mTimer.start();
	}

	public FadeOutController(int startIndex, int lastIndex, boolean noDestroy, Color.HSV flashedColor, int delay, double duration) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mSpeed = delay == 0 ? kZeroSpeed : delay;
		mDuration = duration;
		kPriority = 1;
		mTimer.start();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {

		double n = 1 - ((mTimer.get() % mSpeed) / mSpeed);

		for (int i = mStartIndex; i < mLastIndex; i++) {

			mOutputs.lightingOutput.get(i - mStartIndex).setHSV(mFlashedColor.getH(),
					mFlashedColor.getS(),
					(int) (mFlashedColor.getV() * n));
		}

	}

	@Override
	public boolean checkFinished() {
		return mDuration != -1 && mTimer.get() > mDuration;
	}
}
