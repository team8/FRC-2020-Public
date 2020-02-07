package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import edu.wpi.first.wpilibj.Timer;

public class ConvergingBandsController extends Lighting.LEDController {

	Color.HSV bandColor;
	Color.HSV backgroundColor;
	int mBandLedCount;
	Timer mTimer = new Timer();
	int currentBandPosition;
	double oldTimerValue;

	public ConvergingBandsController(int initIndex, int lastIndex, Color.HSV bandColor, Color.HSV backgroundColor, int bandLedCount) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		this.bandColor = bandColor;
		this.backgroundColor = backgroundColor;
		mBandLedCount = bandLedCount;
		mTimer.start();
//		for (var i = mInitIndex; i < mLastIndex; i++) {
//			mLightingOutputs.lightingOutput.add(new int[] { h, s, v });
//		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() * 6) != oldTimerValue) {
			currentBandPosition += 1;
		}
		for (var i = 0; i < (mLastIndex - mInitIndex) / 2 - 1; i++) {
			if ((i + currentBandPosition) / mBandLedCount % 2 == 0) {
				mLightingOutputs.lightingOutput.get(i)[0] = mLightingOutputs.lightingOutput
						.get(mLastIndex - mInitIndex - i - 1)[0] = bandColor.getH();
				mLightingOutputs.lightingOutput.get(i)[1] = mLightingOutputs.lightingOutput
						.get(mLastIndex - mInitIndex - i - 1)[1] = bandColor.getS();
				mLightingOutputs.lightingOutput.get(i)[2] = mLightingOutputs.lightingOutput
						.get(mLastIndex - mInitIndex - i - 1)[2] = bandColor.getV();
			} else {
				mLightingOutputs.lightingOutput.get(i)[0] = mLightingOutputs.lightingOutput
						.get(mLastIndex - mInitIndex - i - 1)[0] = backgroundColor.getH();
				mLightingOutputs.lightingOutput.get(i)[1] = mLightingOutputs.lightingOutput
						.get(mLastIndex - mInitIndex - i - 1)[1] = backgroundColor.getS();
				mLightingOutputs.lightingOutput.get(i)[2] = mLightingOutputs.lightingOutput
						.get(mLastIndex - mInitIndex - i - 1)[2] = backgroundColor.getV();
			}
		}
		oldTimerValue = Math.round(mTimer.get() * 6);
	}
}
