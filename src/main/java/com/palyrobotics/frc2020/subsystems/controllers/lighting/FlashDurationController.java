package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FlashDurationController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private double mDuration = -1;

	/**
	 * Color flashes with given speed
	 *
	 * @param startIndex   Initial index upon which led patterns should start
	 * @param lastIndex    End index upon which led patterns should stop
	 * @param flashedColor Color to be flashed on white background
	 */

	public FlashDurationController(int startIndex, int lastIndex, Color.HSV flashedColor, double speed) {
		super(startIndex, lastIndex);
		mFlashedColor = flashedColor;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mPriority = 2;
		mTimer.start();
	}

	public FlashDurationController(int startIndex, int lastIndex, Color.HSV flashedColor, double speed, double duration) {
		super(startIndex, lastIndex);
		mFlashedColor = flashedColor;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mDuration = duration;
		mPriority = 2;
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
		return mDuration != -1 && mTimer.hasElapsed(mDuration);
	}
}
