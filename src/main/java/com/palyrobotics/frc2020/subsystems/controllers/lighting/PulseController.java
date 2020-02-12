package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import java.util.Arrays;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class PulseController extends Lighting.LEDController {

	private int mPulseEndIndex;
	private double mSpeed;

	/**
	 * Pulses array of color through entire led strip
	 *
	 * @param initIndex          initial index upon which led patterns should start
	 * @param lastIndex          end index upon which led patterns should stop
	 * @param pulseColorSequence array of values which should move through led strip
	 * @param speed              speed of pulse movement
	 */

	public PulseController(int initIndex, int lastIndex, Color.HSV[] pulseColorSequence, double speed) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex + pulseColorSequence.length - 1;
		mPulseEndIndex = lastIndex;
		mSpeed = speed;
		mTimer.start();
		mOutputs.lightingOutput.addAll(Arrays.asList(pulseColorSequence));
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1) {
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
