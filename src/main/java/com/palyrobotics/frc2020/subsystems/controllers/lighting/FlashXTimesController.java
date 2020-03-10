package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FlashXTimesController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private int mWantedNumOfTimes;
	private int mNumOfTimesFlashedCounter;
	private int enteredCounter = 0;

	protected FlashXTimesController(int startIndex, int lastIndex, Color.HSV flashedColor, int numOfTimes, double delay) {
		super(startIndex, lastIndex);
		mSpeed = delay;
		mFlashedColor = flashedColor;
		mSpeed = delay == 0 ? kZeroSpeed : delay;
		mWantedNumOfTimes = numOfTimes;
		mPriority = 1;
		mTimer.start();
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 == 0) {
			mNumOfTimesFlashedCounter = enteredCounter == 0 ? mNumOfTimesFlashedCounter + 1 : mNumOfTimesFlashedCounter;
			enteredCounter++;
			for (int i = mStartIndex; i < mLastIndex; i++) {
				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(mFlashedColor.getH(), mFlashedColor.getS(),
						mFlashedColor.getV());
			}
		} else {
			enteredCounter = 0;
			for (int i = mStartIndex; i < mLastIndex; i++) {
				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(0, 0, 0);
			}
		}
	}

	@Override
	public boolean checkFinished() {
		return mNumOfTimesFlashedCounter >= mWantedNumOfTimes;
	}
}
