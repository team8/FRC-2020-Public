package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FlashingLightsController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private int mDelay;

	public FlashingLightsController(int initIndex, int lastIndex, Color.HSV flashedColor, int delay) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mDelay = delay;
		mTimer.reset();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (mTimer.hasPeriodPassed(mDelay)) {
			for (int i = mInitIndex; i < mLastIndex; i++) {
				mOutputs.setHSV(i - mInitIndex, mFlashedColor.getH(), mFlashedColor.getS(),
						mFlashedColor.getV());
			}
		} else {
			for (int i = mInitIndex; i < mLastIndex; i++) {
				mOutputs.setHSV(i - mInitIndex, 0, 0, 0);
			}
		}
	}
}
