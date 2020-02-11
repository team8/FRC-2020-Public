package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

public class ColorRangingController extends Lighting.LEDController {

	private int mCurrentHue = 0;
	private boolean mIsHueUpwards;

	public ColorRangingController(int initIndex, int lastIndex) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (mCurrentHue >= 200) {
			mIsHueUpwards = false;
		}
		if (mCurrentHue <= 0) {
			mIsHueUpwards = true;
		}
		mCurrentHue = mIsHueUpwards ? mCurrentHue++ : mCurrentHue--;

		for (var i = 0; i < mOutputs.getLength(); i++) {
			mOutputs.setHSV(i, mCurrentHue, 120, 120);
		}
	}
}
