package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Spinner extends Subsystem {

	public enum SpinnerState {
		TO_COLOR, SPIN, IDLE
	}

	private static final SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
	private static Spinner sInstance = new Spinner();
	ControllerOutput mOutput = new ControllerOutput();

	public static Spinner getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		SpinnerState spinnerState = commands.spinnerWantedState;
		switch (spinnerState) {
			case IDLE:
				mOutput.setIdle();
				break;
			case SPIN:

				break;
			case TO_COLOR:

				break;
		}

	}

	/**
	 * Provides 'vector' that signifies direction and magnitude to goal color in
	 * most efficient path.
	 *
	 * @param currentColor current color being detected by color string in string
	 *                     format
	 * @return int[0]: positive distance in color change units (number of colors
	 *         changes till goal color found) int[1]: spinner movement direction. 1
	 *         corresponds to clockwise, -1 corresponds to anticlockwise
	 */
	public int directionToGoalColor(String currentColor, String gameTargetColor) {
		int gameDataIndex = SpinnerConstants.controlPanelColorOrder.indexOf(gameTargetColor);
		int currentColorIndex = SpinnerConstants.controlPanelColorOrder.indexOf(currentColor);

		if (((gameDataIndex - currentColorIndex) % 4) <= 2) {
//			mVectorToColor[0] = (gameDataIndex - currentColorIndex) % 4;
//			mVectorToColor[1] = -1;
			return -1;
		} else {
//			mVectorToColor[0] = 4 - ((gameDataIndex - currentColorIndex) % 4);
//			mVectorToColor[1] = 1;
			return 1;
		}
//		return mVectorToColor;
//		return gameDataIndex;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}
}
