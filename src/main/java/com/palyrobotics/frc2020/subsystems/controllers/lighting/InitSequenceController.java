package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;
	private double mSpeed;

	/**
	 * Animation in which entire h component in hsv is displayed.
	 *
	 * @param initIndex initial index upon which led patterns should start
	 * @param lastIndex end index upon which led patterns should stop
	 * @param speed     speed of animation
	 */

	public InitSequenceController(int initIndex, int lastIndex, double speed) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = mInitIndex;
		mTimer.start();
		mSpeed = speed == 0 ? 0.001 : speed;
		for (var i = initIndex; i < lastIndex; i++) {
			mOutputs.lightingOutput.add(new Color.HSV(0, 0, 0));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1 && mCurrentLedIndex < mLastIndex) {
			mOutputs.lightingOutput.get(mCurrentLedIndex - mInitIndex).setHSV((int) ((mCurrentLedIndex - mInitIndex) * Math.log(mCurrentLedIndex) +
					5), 247, 100);
			mCurrentLedIndex += 1;
			mTimer.reset();
		}
	}

}
