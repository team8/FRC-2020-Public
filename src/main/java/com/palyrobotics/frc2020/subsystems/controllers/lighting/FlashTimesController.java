package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FlashTimesController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private int mWantedNumOfTimes;
	private int mNumOfTimesFlashedCounter;
	private int mEnteredCounter = 0;

	public FlashTimesController(int startIndex, int lastIndex, Color.HSV flashedColor, int numOfTimes, double speed) {
		super(startIndex, lastIndex);
		mSpeed = speed;
		mFlashedColor = flashedColor;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mWantedNumOfTimes = numOfTimes;
		mPriority = 1;
		mWantsReset = false;
		mTimer.start();
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 == 0) {
			mNumOfTimesFlashedCounter = mEnteredCounter == 0 ? mNumOfTimesFlashedCounter + 1 : mNumOfTimesFlashedCounter;
			mEnteredCounter++;
			for (int i = mStartIndex; i < mLastIndex; i++) {
				mOutputs.lightingOutput.get(i - mStartIndex).setHSV(mFlashedColor.getH(), mFlashedColor.getS(),
						mFlashedColor.getV());
			}
		} else {
			mEnteredCounter = 0;
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
