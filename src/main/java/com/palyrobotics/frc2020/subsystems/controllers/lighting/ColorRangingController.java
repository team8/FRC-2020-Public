package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

public class ColorRangingController extends Lighting.LEDController {

	private int mCurrentHue = 0;

	/**
	 * Slides through hsv spectrum and applies color to led strip
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 */

	public ColorRangingController(int startIndex, int lastIndex) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		kPriority = 4;
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (mCurrentHue >= 175) {
			mCurrentHue = 0;
		}
		mCurrentHue++;

		for (var i = 0; i < mOutputs.lightingOutput.size(); i++) {
			mOutputs.lightingOutput.get(i).setHSV(mCurrentHue, 247, 83);
		}
	}
}
