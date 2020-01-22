package com.palyrobotics.frc2020.behavior.routines.spinner;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Spinner;

public class SpinnerRotationControlRoutine extends SpinnerRoutineBase {

	private String mPreviousColor;
	private int mColorChangeCounter;

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		String currentColor = state.closestColorString;
		mColorChangeCounter = !currentColor.equals(mPreviousColor) ? mColorChangeCounter++ : mColorChangeCounter;
		mPreviousColor = currentColor;
		commands.spinnerWantedState = Spinner.SpinnerState.ROTATION_CONTROL;
	}

	@Override
	public boolean checkFinished() {
		return mColorChangeCounter > mConfig.rotationControlColorChangeRequirementCount;
	}
}
