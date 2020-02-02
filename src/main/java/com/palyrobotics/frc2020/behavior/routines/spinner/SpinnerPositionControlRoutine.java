package com.palyrobotics.frc2020.behavior.routines.spinner;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class SpinnerPositionControlRoutine extends RoutineBase {

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
		commands.spinnerWantedState = Spinner.State.ROTATING;
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mTargetColor.equals(mCurrentColor);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mSpinner);
	}
}
