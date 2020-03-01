package com.palyrobotics.frc2020.behavior.routines.spinner;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class SpinnerPositionControlRoutine extends RoutineBase {

	private String mCurrentColor, mTargetColor;
	private int mDirectionToGoalColor;
	private boolean mIsFinished;

	@Override
	protected void start(Commands commands, @ReadOnly RobotState state) {
		mDirectionToGoalColor = mSpinner.directionToGoalColor(state.closestColorString, state.gameData);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		mTargetColor = state.gameData;
		mCurrentColor = state.closestColorString;
		System.out.println(mDirectionToGoalColor);

		if (mDirectionToGoalColor < 0) {
			commands.spinnerWantedState = Spinner.State.ROTATING_LEFT;
		} else if (mDirectionToGoalColor > 0) {
			commands.spinnerWantedState = Spinner.State.ROTATING_RIGHT;
		}
		mIsFinished = mTargetColor.equals(mCurrentColor);
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.lightingWantedState = Lighting.State.SPINNER_DONE;
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mIsFinished;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.spinnerWantedState = Spinner.State.IDLE;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mSpinner);
	}
}
