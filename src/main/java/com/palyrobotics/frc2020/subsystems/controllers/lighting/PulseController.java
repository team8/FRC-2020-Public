package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import java.util.List;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class PulseController extends Lighting.LEDController {

	private int mPulseEndIndex;

	/**
	 * Pulses array of color through entire led strip
	 *
	 * @param startIndex         initial index upon which led patterns should start
	 * @param lastIndex          end index upon which led patterns should stop
	 * @param pulseColorSequence array of values which should move through led strip
	 * @param speed              speed of pulse movement
	 */

	public PulseController(int startIndex, int lastIndex, List<Color.HSV> pulseColorSequence, double speed) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex + pulseColorSequence.size() - 1;
		mPulseEndIndex = lastIndex;
		kZeroSpeed = speed == 0 ? kZeroSpeed : speed;
		mTimer.start();
		mOutputs.lightingOutput.addAll(pulseColorSequence);
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / kZeroSpeed) % 2 == 1) {
			mStartIndex += 1;
			mLastIndex += 1;
			mTimer.reset();
		}
	}

	@Override
	public boolean checkFinished() {
		return mLastIndex >= mPulseEndIndex;
	}
}
