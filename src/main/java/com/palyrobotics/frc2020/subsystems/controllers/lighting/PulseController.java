package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class PulseController extends Lighting.LEDController {

	private Color.HSV[] mPulse;
	private int mPulseEndIndex;
	private double mSpeed;

	public PulseController(int startIndex, int endIndex, Color.HSV[] pulseColorSequence, double speed) {
		mPulse = pulseColorSequence;
		mInitIndex = startIndex;
		mLastIndex = startIndex + mPulse.length - 1;
		mPulseEndIndex = endIndex;
		mSpeed = speed;
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (mTimer.hasPeriodPassed(1 / mSpeed)) {
			//TODO Reimplement
//			for (int i = mInitIndex; i < mLastIndex; i++) {
//				for (Color.HSV currentColor : mPulse) {
//					mOutputs.lightingOutput
//							.add(new Color.HSV(currentColor.getH(), currentColor.getS(), currentColor.getV()));
//				}
//			}
//			mInitIndex += 1;
//			mLastIndex += 1;
		}
	}

	@Override
	public boolean checkFinished() {
		return mLastIndex >= mPulseEndIndex;
	}
}
