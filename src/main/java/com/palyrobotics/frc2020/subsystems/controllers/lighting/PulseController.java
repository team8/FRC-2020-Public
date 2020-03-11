package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import java.util.List;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class PulseController extends Lighting.LEDController {

	private int mPulseEndIndex;
	private int mPulseStartIndex;
	private boolean mIsColorsInitialized;
	private List<Color.HSV> mPulse;
	private boolean mIsReversed;

	/**
	 * Pulses array of color through entire led strip
	 *
	 * @param startIndex         Initial index upon which led patterns should start
	 * @param lastIndex          End index upon which led patterns should stop
	 * @param pulseColorSequence Array of values which should move through led strip
	 * @param speed              Speed of pulse movement
	 */

	public PulseController(int startIndex, int lastIndex, List<Color.HSV> pulseColorSequence, double speed) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = startIndex + pulseColorSequence.size() - 1;
		mPulseEndIndex = lastIndex;
		mPulseStartIndex = startIndex;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mPulse = pulseColorSequence;
		mPriority = 0;
		if (lastIndex <= startIndex) {
			mIsReversed = true;
		}
		mTimer.start();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (!mIsColorsInitialized) {
			mOutputs.lightingOutput.clear();
			mOutputs.lightingOutput.addAll(mPulse);
			mIsColorsInitialized = true;
		}
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1) {
			if (mIsReversed) {
				mStartIndex -= 1;
				mLastIndex -= 1;
			} else {
				mStartIndex += 1;
				mLastIndex += 1;
			}
			mTimer.reset();
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof PulseController) {
			PulseController otherPulseController = (PulseController) object;
			return otherPulseController.mPulseStartIndex == this.mPulseStartIndex && otherPulseController.mPulseEndIndex == this.mPulseEndIndex && otherPulseController.mPulse.equals(this.mPulse);
		}
		return false;
	}

	@Override
	public boolean checkFinished() {
		if (mIsReversed) {
			return mLastIndex <= mPulseEndIndex;
		} else {
			return mLastIndex >= mPulseEndIndex;
		}
	}
}
