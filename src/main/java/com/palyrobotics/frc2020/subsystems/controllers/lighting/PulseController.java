package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

import edu.wpi.first.wpilibj.Timer;

public class PulseController extends Lighting.LEDController {

	private Color.HSV[] mPulse;
	private int mPulseEndIndex;
	private double mPulseSpeed;
	private Timer mTimer = new Timer();

	public PulseController(int startIndex, int endIndex, double pulseMoveSpeed, Color.HSV[] pulse) {
		mPulse = pulse;
		mInitIndex = startIndex;
		mLastIndex = startIndex + mPulse.length - 1;
		mPulseEndIndex = endIndex;
		mPulseSpeed = pulseMoveSpeed;
		mTimer.start();
		for (Color.HSV currentColor : pulse) {
			mOutputs.lightingOutput
					.add(Color.HSV.getNewInstance(currentColor.getH(), currentColor.getS(), currentColor.getV()));
		}
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() * mPulseSpeed) % 2 == 0) {
			mInitIndex += 1;
			mLastIndex += 1;
		}
	}

	@Override
	public boolean checkFinished() {
		return mLastIndex >= mPulseEndIndex;
	}
}
