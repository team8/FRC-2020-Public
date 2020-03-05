package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Spinner extends SubsystemBase {

	public enum State {
		IDLE, ROTATING_LEFT, ROTATING_RIGHT
	}

	private static final SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
	private static Spinner sInstance = new Spinner();
	private ControllerOutput mOutput = new ControllerOutput();

	public static Spinner getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		State spinnerState = commands.spinnerWantedState;
		switch (spinnerState) {
			case IDLE:
				mOutput.setIdle();
				break;
			case ROTATING_LEFT:
				mOutput.setPercentOutput(commands.spinnerWantedPercentOutput);
				break;
			case ROTATING_RIGHT:
				mOutput.setPercentOutput(-commands.spinnerWantedPercentOutput);
				break;
		}
	}

	/**
	 * Provides most efficient direction to goal color.
	 *
	 * @param  currentColor    Current color being detected by color string in string format
	 * @param  gameTargetColor Color to find given by FMS
	 * @return                 int denoting direction wheel needs to move. 1 corresponds to clockwise,
	 *                         -1 corresponds to anticlockwise
	 */
	public int directionToGoalColor(String currentColor, String gameTargetColor) {
		int gameDataIndex = SpinnerConstants.kControlPanelColorOrder.indexOf(gameTargetColor),
				currentColorIndex = SpinnerConstants.kControlPanelColorOrder.indexOf(currentColor);
//		System.out.println((gameDataIndex - currentColorIndex) % 4);
		return (gameDataIndex - currentColorIndex) % 4 < 2 ? -1 : 1;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}
}
