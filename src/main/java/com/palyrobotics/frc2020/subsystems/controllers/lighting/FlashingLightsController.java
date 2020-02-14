package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FlashingLightsController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private int mDuration = -1;

	/**
	 * Color flashes with given delay
	 *
	 * @param startIndex   Initial index upon which led patterns should start
	 * @param lastIndex    End index upon which led patterns should stop
	 * @param flashedColor Color to be flashed on white background
	 */

	public FlashingLightsController(int startIndex, int lastIndex, Color.HSV flashedColor, int delay) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mSpeed = delay == 0 ? kZeroSpeed : delay;
		mTimer.start();
	}

	public FlashingLightsController(int startIndex, int lastIndex, Color.HSV flashedColor, int delay, int duration) {
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
		if (Math.round(mTimer.get() / mSpeed) % 2 == 0) {
			for (int i = mStartIndex; i < mLastIndex; i++) {
				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(mFlashedColor.getH(), mFlashedColor.getS(),
						mFlashedColor.getV());
			}
		} else {
			for (int i = mStartIndex; i < mLastIndex; i++) {
				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(0, 0, 0);
			}
		}
	}

	@Override
	public boolean checkFinished() {
		return mDuration != -1 && mTimer.hasPeriodPassed(mDuration);
	}
}
