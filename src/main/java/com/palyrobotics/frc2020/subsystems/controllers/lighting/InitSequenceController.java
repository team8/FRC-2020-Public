package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;

	/**
	 * Animation in which entire h component in hsv is displayed.
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 * @param speed      Speed of animation
	 */

	public InitSequenceController(int startIndex, int lastIndex, double speed) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = mStartIndex;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		kPriority = 4;
		mTimer.start();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1 && mCurrentLedIndex < mLastIndex) {
			mOutputs.lightingOutput.get(mCurrentLedIndex - mStartIndex).setHSV((int) ((mCurrentLedIndex - mStartIndex) * Math.log(mCurrentLedIndex) +
					5), 247, 100);
			mCurrentLedIndex += 1;
			mTimer.reset();
		}
	}

}
