package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import edu.wpi.first.wpilibj.Timer;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;

	public InitSequenceController(int initIndex, int lastIndex) {
		mInitIndex = initIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = initIndex;
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (mCurrentLedIndex <= mLastIndex) {
			mLightingOutputs.lightingOutput
					.add(Color.HSV.getNewInstance((int) ((mCurrentLedIndex - mInitIndex) * Math.log(mCurrentLedIndex) +
							5), 247, 100));
			mCurrentLedIndex += 1;
		}
		Timer.delay(0.07);
	}
}
