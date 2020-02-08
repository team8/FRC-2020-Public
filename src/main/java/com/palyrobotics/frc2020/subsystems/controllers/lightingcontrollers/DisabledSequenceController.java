package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class DisabledSequenceController extends Lighting.LEDController {

	private int mCurrentHue = 0;

	public DisabledSequenceController(int initIndex, int lastIndex) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		for (var i = initIndex; i <= lastIndex; i++) {
			mLightingOutputs.lightingOutput.add(Color.HSV.getNewInstance(mCurrentHue, 247, 87));
		}
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		mCurrentHue = mCurrentHue >= 200 ? mCurrentHue-- : mCurrentHue++;

		for (var i = 0; i < mLightingOutputs.lightingOutput.size(); i++) {
			mLightingOutputs.lightingOutput.get(i).setH(mCurrentHue);
		}
	}
}
