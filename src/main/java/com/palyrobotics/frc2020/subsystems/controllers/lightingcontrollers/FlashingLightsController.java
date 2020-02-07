package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import edu.wpi.first.wpilibj.Timer;

public class FlashingLightsController extends Lighting.LEDController {

	private Timer mTimer = new Timer();
	private Color.HSV mFlashedColor;
	private int mDelayFactor;

	public FlashingLightsController(int initIndex, int lastIndex, Color.HSV flashedColor, int delayFactor) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mFlashedColor = flashedColor;
		mDelayFactor = delayFactor;
		mTimer.start();
		for (var i = mInitIndex; i < mLastIndex; i++) {
			mLightingOutputs.lightingOutput
					.add(new int[] { mFlashedColor.getH(), mFlashedColor.getS(), mFlashedColor.getV() });
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		double time = Math.round(mTimer.get() * mDelayFactor);
		if (time % 2 == 0) {
			for (int i = mInitIndex; i < mLastIndex; i++) {
				mLightingOutputs.lightingOutput.get(i - mInitIndex)[0] = mFlashedColor.getH();
				mLightingOutputs.lightingOutput.get(i - mInitIndex)[1] = mFlashedColor.getS();
				mLightingOutputs.lightingOutput.get(i - mInitIndex)[2] = mFlashedColor.getV();
			}
		} else {
			for (int i = mInitIndex; i < mLastIndex; i++) {
				mLightingOutputs.lightingOutput.get(i - mInitIndex)[0] = mLightingOutputs.lightingOutput
						.get(i - mInitIndex)[1] = mLightingOutputs.lightingOutput.get(i - mInitIndex)[2] = 0;
			}
		}
	}
}
