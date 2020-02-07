package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

import edu.wpi.first.wpilibj.Timer;

public class ConvergingBandsController extends Lighting.LEDController {

	int mBackHue;
	int mBackSat;
	int mBackVal;
	int mForeHue;
	int mForeSat;
	int mForeVal;
	int mBandLedCount;
	Timer mTimer = new Timer();
	int currentBandPosition;
	double oldTimerValue;

	public ConvergingBandsController(int initIndex, int lastIndex, int h, int s, int v, int h2, int s2, int v2, int bandLedCount) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mBackHue = h;
		mBackSat = s;
		mBackVal = v;
		mForeHue = h2;
		mForeSat = s2;
		mForeVal = v2;
		mBandLedCount = bandLedCount;
		mTimer.start();
		for (var i = mInitIndex; i < mLastIndex; i++) {
			mLightingOutputs.lightingOutput.add(new int[] { h, s, v });
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() * 6) != oldTimerValue) {
			currentBandPosition += 1;
		}
		for (var i = 0; i < (mLastIndex - mInitIndex) / 2 - 1; i++) {
			if ((i + currentBandPosition) / mBandLedCount % 2 == 0) {
				mLightingOutputs.lightingOutput.get(i)[0] = mForeHue;
				mLightingOutputs.lightingOutput.get(i)[1] = mForeSat;
				mLightingOutputs.lightingOutput.get(i)[2] = mForeVal;
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1)[0] = mForeHue;
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1)[1] = mForeSat;
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1)[2] = mForeVal;
			} else {
				mLightingOutputs.lightingOutput.get(i)[0] = mBackHue;
				mLightingOutputs.lightingOutput.get(i)[1] = mBackSat;
				mLightingOutputs.lightingOutput.get(i)[2] = mBackVal;
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1)[0] = mBackHue;
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1)[1] = mBackSat;
				mLightingOutputs.lightingOutput.get(mLastIndex - mInitIndex - i - 1)[2] = mBackVal;
			}
		}
		oldTimerValue = Math.round(mTimer.get() * 6);
	}
}
