package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.behavior.routines.spinner.PositionControlRoutine;
import com.palyrobotics.frc2020.behavior.routines.spinner.RotationControlRoutine;
import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Spinner extends Subsystem {

	public enum SpinnerState {
		IDLE, ROT_CONTROL, POS_CONTROL
	}

	private static final SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
	private static Spinner sInstance = new Spinner();
	ControllerOutput mOutput = new ControllerOutput();
	private Spinner.SpinnerState mSpinnerState;


	@Override
	public void update(Commands commands, RobotState robotState) {
		mSpinnerState = commands.spinnerWantedState;
		switch(mSpinnerState) {
			case IDLE:
				mOutput.setIdle();
			case ROT_CONTROL:
				commands.addWantedRoutine(new RotationControlRoutine());
			case POS_CONTROL:
				commands.addWantedRoutine(new PositionControlRoutine());
		}

	}

	public static Spinner getInstance() {
		return sInstance;
	}

	/**
	 * Provides most efficient direction to goal color
	 *
	 * @param currentColor    current color being detected by color string in string
	 *                        format
	 * @param gameTargetColor color to find given by FMS
	 * @return int denoting direction wheel needs to move. 1 corresponds to
	 *         clockwise, -1 corresponds to anticlockwise
	 */
	public int directionToGoalColor(String currentColor, String gameTargetColor) {
		int gameDataIndex = SpinnerConstants.controlPanelColorOrder.indexOf(gameTargetColor);
		int currentColorIndex = SpinnerConstants.controlPanelColorOrder.indexOf(currentColor);

		if (((gameDataIndex - currentColorIndex) % 4) <= 2) {
			return -1;
		} else {
			return 1;
		}
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}
}
