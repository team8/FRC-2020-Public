package com.palyrobotics.frc2020.behavior.routines.spinner;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Spinner;

public class SpinnerPositionControlRoutine extends SpinnerRoutineBase {

	private String mCurrentColor, mTargetColor;
	private int mDirectionToGoalColor;

	@Override
	protected void start(Commands commands, @ReadOnly RobotState state) {
		mDirectionToGoalColor = mSpinner.directionToGoalColor(mCurrentColor, mTargetColor);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		mTargetColor = state.gameData;
		mCurrentColor = state.closestColorString;
		commands.spinnerWantedState = Spinner.SpinnerState.POSITION_CONTROL;
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mTargetColor.equals(mCurrentColor);
	}
}
