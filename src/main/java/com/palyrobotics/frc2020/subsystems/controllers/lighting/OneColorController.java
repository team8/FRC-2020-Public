package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class OneColorController extends Lighting.LEDController {

	private Color.HSV mColor;

	/**
	 * Single color, no animation, led controller
	 *
	 * @param startIndex initial index upon which led patterns should start
	 * @param lastIndex  end index upon which led patterns should stop
	 * @param color      color to be displayed
	 */

	public OneColorController(int startIndex, int lastIndex, Color.HSV color) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mColor = color;
		for (var i = mStartIndex; i < mLastIndex; i++) {
			mOutputs.lightingOutput
					.add(new Color.HSV(mColor.getH(), mColor.getS(), mColor.getV()));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		for (int i = mStartIndex; i < mLastIndex; i++) {
			mOutputs.lightingOutput.get(i).setHSV(mColor.getH(), mColor.getS(), mColor.getV());
		}
	}
}
