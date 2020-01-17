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
	private ControllerOutput mOutput = new ControllerOutput();
	private int[] mVectorToColor = new int[2];

	public static Spinner getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		SpinnerState spinnerState = commands.spinnerWantedState;
		String currentColor = robotState.closestColorString;
		switch (spinnerState) {
			case IDLE:
				mOutput.setIdle();
				break;
			case SPIN:
				mOutput.setTargetPositionProfiled(mConfig.rotSetPoint * SpinnerConstants.eighthCPMovementGearRatio,
						mConfig.profiledRotControlVelocityGains);
				break;
			case TO_COLOR:
				mVectorToColor = vectorToGoalColor(currentColor);
				mOutput.setTargetPositionProfiled(
						mVectorToColor[0] * mVectorToColor[1] * SpinnerConstants.eighthCPMovementGearRatio,
						mConfig.profiledPosControlVelocityGains);
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
	public int[] vectorToGoalColor(String currentColor) {
		int gameDataIndex = SpinnerConstants.controlPanelColorOrder.indexOf(RobotState.getInstance().gameData);
		int currentColorIndex = SpinnerConstants.controlPanelColorOrder.indexOf(currentColor);

		if (((gameDataIndex - currentColorIndex) % 4) <= 2) {
			mVectorToColor[0] = (gameDataIndex - currentColorIndex) % 4;
			mVectorToColor[1] = -1;
		} else {
			mVectorToColor[0] = 4 - ((gameDataIndex - currentColorIndex) % 4);
			mVectorToColor[1] = 1;
		}
		return mVectorToColor;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}
}
