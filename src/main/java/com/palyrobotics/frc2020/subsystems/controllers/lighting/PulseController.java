package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import java.util.List;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import javax.print.attribute.HashPrintJobAttributeSet;

public class PulseController extends Lighting.LEDController {

	public int mPulseEndIndex;
	public int mPulseStartIndex;
	private boolean mIsColorsInitialized;
	private List<Color.HSV> mPulse;

	/**
	 * Pulses array of color through entire led strip
	 *
	 * @param startIndex         Initial index upon which led patterns should start
	 * @param lastIndex          End index upon which led patterns should stop
	 * @param pulseColorSequence Array of values which should move through led strip
	 * @param speed              Speed of pulse movement
	 */

	public PulseController(int startIndex, int lastIndex, boolean noDestroy, List<Color.HSV> pulseColorSequence, double speed) {
		super(startIndex, lastIndex, noDestroy);
		mStartIndex = startIndex;
		mLastIndex = startIndex + pulseColorSequence.size() - 1;
		mPulseEndIndex = lastIndex;
		mPulseStartIndex = startIndex;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		mTimer.start();
		mPulse = pulseColorSequence;
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (!mIsColorsInitialized) {
			mOutputs.lightingOutput.clear();
			mOutputs.lightingOutput.addAll(mPulse);
			mIsColorsInitialized = true;
		}
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1) {
			mStartIndex += 1;
			mLastIndex += 1;
			mTimer.reset();
		}
	}

	@Override
	public boolean equals(Object object){
		if (object instanceof PulseController) {
			PulseController otherPulseController = (PulseController) object;
			return otherPulseController.mPulseStartIndex == this.mPulseStartIndex && otherPulseController.mPulseEndIndex == this.mPulseEndIndex && otherPulseController.mPulse.equals(this.mPulse);
		}
		return false;
	}

	@Override
	public boolean checkFinished() {
		return mLastIndex >= mPulseEndIndex;
	}
}
