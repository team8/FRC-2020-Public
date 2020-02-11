package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import java.util.Arrays;

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
		mTimer.start();
		mOutputs.lightingOutput.addAll(Arrays.asList(pulseColorSequence));
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get()/mSpeed) % 2 == 1) {
			mInitIndex += 1;
			mLastIndex += 1;
			mTimer.reset();
		}
	}

	@Override
	public boolean checkFinished() {
		return mLastIndex >= mPulseEndIndex;
	}
}
