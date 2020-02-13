package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FlashingLightsController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;

	/**
	 * Color flashes with given delay
	 *
	 * @param startIndex   initial index upon which led patterns should start
	 * @param lastIndex    end index upon which led patterns should stop
	 * @param flashedColor color to flashed on white background
	 */

	public FlashingLightsController(int startIndex, int lastIndex, Color.HSV flashedColor, int delay) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		kZeroSpeed = delay == 0 ? kZeroSpeed : delay;
		mTimer.start();
		for (var i = mStartIndex; i < mLastIndex; i++) {
			mOutputs.lightingOutput
					.add(new Color.HSV(mFlashedColor.getH(), mFlashedColor.getS(), mFlashedColor.getV()));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / kZeroSpeed) % 2 == 0) {
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
}
