package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;
	private double mSpeed;

	public InitSequenceController(int initIndex, int lastIndex, double speed) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = initIndex;
		mTimer.start();
		mSpeed = speed == 0 ? 0.001 : speed;
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get()/mSpeed) % 2 == 1 && mCurrentLedIndex <= mLastIndex) {
			mOutputs.lightingOutput
					.add(new Color.HSV((int) ((mCurrentLedIndex - mInitIndex) * Math.log(mCurrentLedIndex) +
							5), 247, 100));
			mCurrentLedIndex += 1;
			mTimer.reset();
		}
	}

}
