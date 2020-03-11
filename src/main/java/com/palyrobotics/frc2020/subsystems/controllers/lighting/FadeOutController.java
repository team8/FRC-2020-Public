package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class FadeOutController extends Lighting.LEDController {

	private Color.HSV mFlashedColor;
	private double mDuration = -1;

	/**
	 * Color flashes with given speed
	 *
	 * @param startIndex   Initial index upon which led patterns should start
	 * @param lastIndex    End index upon which led patterns should stop
	 * @param flashedColor Color to be flashed on white background
	 */

	public FadeOutController(int startIndex, int lastIndex, boolean noDestroy, Color.HSV flashedColor, int speed) {
		super(startIndex, lastIndex);
		mFlashedColor = flashedColor;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mPriority = 1;
		mTimer.start();
	}

	public FadeOutController(int startIndex, int lastIndex, boolean noDestroy, Color.HSV flashedColor, int speed, double duration) {
		super(startIndex, lastIndex);
		mFlashedColor = flashedColor;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mDuration = duration;
		mPriority = 1;
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
