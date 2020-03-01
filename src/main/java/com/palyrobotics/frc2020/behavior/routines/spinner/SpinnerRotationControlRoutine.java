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
	private int mColorChangeCounter = 0;
	private boolean mIsFinished;

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		String currentColor = state.closestColorString;
		if (!currentColor.equals(mPreviousColor)) {
			mColorChangeCounter++;
		}
		mPreviousColor = currentColor;
		commands.spinnerWantedState = Spinner.State.ROTATING_RIGHT;
		mIsFinished = mColorChangeCounter > mConfig.rotationControlColorChangeRequirementCount;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.lightingWantedState = Lighting.State.SPINNER_DONE;
		super.stop(commands, state);
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
