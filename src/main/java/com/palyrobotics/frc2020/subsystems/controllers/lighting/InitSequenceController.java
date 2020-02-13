package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;

	/**
	 * Animation in which entire h component in hsv is displayed.
	 *
	 * @param startIndex initial index upon which led patterns should start
	 * @param lastIndex  end index upon which led patterns should stop
	 * @param speed      speed of animation
	 */

	public InitSequenceController(int startIndex, int lastIndex, double speed) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = mStartIndex;
		mTimer.start();
		kZeroSpeed = speed == 0 ? kZeroSpeed : speed;
		for (var i = startIndex; i < lastIndex; i++) {
			mOutputs.lightingOutput.add(new Color.HSV(0, 0, 0));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / kZeroSpeed) % 2 == 1 && mCurrentLedIndex < mLastIndex) {
			mOutputs.lightingOutput.get(mCurrentLedIndex - mStartIndex).setHSV((int) ((mCurrentLedIndex - mStartIndex) * Math.log(mCurrentLedIndex) +
					5), 247, 100);
			mCurrentLedIndex += 1;
			mTimer.reset();
		}
	}

}
