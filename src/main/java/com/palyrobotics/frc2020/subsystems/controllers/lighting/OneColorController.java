package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class OneColorController extends Lighting.LEDController {

	private Color.HSV mColor;

	public OneColorController(int initIndex, int lastIndex, Color.HSV color) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mColor = color;
		for (var i = mInitIndex; i < mLastIndex; i++) {
			mOutputs.lightingOutput
					.add(new Color.HSV(mColor.getH(), mColor.getS(), mColor.getV()));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		for (int i = mInitIndex; i < mLastIndex; i++) {
			mOutputs.lightingOutput.get(i).setHSV(mColor.getH(), mColor.getS(), mColor.getV());
		}
	}
}
