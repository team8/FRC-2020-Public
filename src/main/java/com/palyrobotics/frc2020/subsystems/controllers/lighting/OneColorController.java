package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.util.Color;

public class OneColorController extends Lighting.LEDController {

	private Color.HSV mColor;
	private double mDuration = -1;


	public OneColorController() {
		super(0, 28);
		mStartIndex = 0;
		mLastIndex = 28;
	}

	/**
	 * Single color, no animation, led controller
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 * @param color      Color to be displayed
	 */
	public void initallize(int startIndex, int lastIndex, Color.HSV color, double duration)
	{
		isOn = true;
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mColor = color;
		mDuration = duration;
		kPriority = 3;
		mTimer.start();
	}
	
	@Override
	public void updateSignal(Commands commands, RobotState state) {
		for (int i = 0; i < mOutputs.lightingOutput.size(); i++) {
			mOutputs.lightingOutput.get(i).setHSV(mColor.getH(), mColor.getS(), mColor.getV());
		}
	}

	@Override
	public boolean checkFinished() {
		if(mDuration != -1 && mTimer.hasElapsed(mDuration))
		{
			isOn = false;
			return true;
		}
		return false;
	}
}
