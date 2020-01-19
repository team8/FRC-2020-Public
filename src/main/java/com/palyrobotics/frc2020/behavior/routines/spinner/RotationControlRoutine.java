package com.palyrobotics.frc2020.behavior.routines.spinner;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class RotationControlRoutine extends RoutineBase {

	private static final SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
	private ControllerOutput mOutput = Spinner.getInstance().getOutput();
	private Spinner mSpinner = new Spinner();
	private String mCurrentColor, mPreviousColor;
	public int mColorChangeCounter = 0;

	@Override
	public String toString() {
		return getName();
	}

	protected void update(Commands commands) {
		mOutput.setPercentOutput(mConfig.rotationControlOutput);
		mCurrentColor = RobotState.getInstance().closestColorString;
		mColorChangeCounter = !mCurrentColor.equals(mPreviousColor) ? mColorChangeCounter++ : mColorChangeCounter;
		mPreviousColor = mCurrentColor;
	}

	public boolean checkFinished() {
		return mColorChangeCounter > mConfig.rotationControlColorChangeRequirement;
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public Set<Subsystem> getRequiredSubsystems() {
		return Set.of(mSpinner);
	}
}
