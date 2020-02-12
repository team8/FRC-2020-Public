package com.palyrobotics.frc2020.behavior.routines.spinner;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;

public class SpinnerRotationControlRoutine extends RoutineBase {

	private SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
	private String mPreviousColor;
	private int mColorChangeCounter;
	private boolean mIsFinished;

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		String currentColor = state.closestColorString;
		mColorChangeCounter = !currentColor.equals(mPreviousColor) ? mColorChangeCounter++ : mColorChangeCounter;
		mPreviousColor = currentColor;
		commands.spinnerWantedState = Spinner.State.ROTATING;
		mIsFinished = mColorChangeCounter > mConfig.rotationControlColorChangeRequirementCount;
		commands.lightingWantedState = mIsFinished ? Lighting.State.SPINNER_DONE : commands.lightingWantedState;
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mIsFinished;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mSpinner);
	}
}
