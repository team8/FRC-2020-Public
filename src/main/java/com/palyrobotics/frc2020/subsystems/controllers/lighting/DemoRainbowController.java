package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

public class DemoRainbowController extends Lighting.LEDController {

	private int mCurrentHue = 0;
	private int kOffset = 0;
	private double kLastNum = 0;

	/**
	 * Slides through hsv spectrum and applies color to led strip
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 */

	public DemoRainbowController(int startIndex, int lastIndex) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		kPriority = 4;
		mTimer.start();
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		for (var i = 0; i < mOutputs.lightingOutput.size(); i++) {
			double hue = (i + kOffset) * (175 / (double) (mOutputs.lightingOutput.size()));
			if (hue > 175) {
				hue -= 175;
			}
			mOutputs.lightingOutput.get(i).setHSV((int) hue, 247, 83);
		}
		if (/*Math.round(mTimer.get()/0.3) % 2 == 0 && */Math.round(mTimer.get() / 0.07) != kLastNum) {
			kLastNum = Math.round(mTimer.get() / 0.07);
			kOffset += 1;
		}
		if (kOffset >= mOutputs.lightingOutput.size()) {
			kOffset = 0;
		}
		if (mCurrentHue >= 175) {
			mCurrentHue = 0;
		}
	}
}
