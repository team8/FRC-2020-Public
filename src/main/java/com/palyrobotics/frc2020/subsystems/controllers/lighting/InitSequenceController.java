package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;
	private double mSpeed;

	public InitSequenceController(int initIndex, int lastIndex, int speed) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = initIndex;
		mSpeed = speed == 0 ? 0.001 : speed;
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (mTimer.hasPeriodPassed(1 / mSpeed) && mCurrentLedIndex <= mLastIndex) {
			mOutputs.setHSV(mCurrentLedIndex, (int) ((mCurrentLedIndex - mInitIndex) * Math.log(mCurrentLedIndex) +
					5), 247, 100);
			mCurrentLedIndex += 1;
		}
	}

}
