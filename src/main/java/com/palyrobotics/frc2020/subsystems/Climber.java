package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Climber extends Subsystem {

	public enum ClimberState {
		CLIMBING, ADJUSTING_LEFT, ADJUSTING_RIGHT, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mOutput;
	private ControllerOutput mAdjustingOutput;
	private ClimberConfig mConfig = Configs.get(ClimberConfig.class);
	private double mWantedOutput;

	private Climber() {
	}

	public static Climber getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		ClimberState state = commands.climberWantedState;
		switch (state) {
			case CLIMBING:
				mWantedOutput = commands.getClimberWantedOutput();
				mOutput.setPercentOutput(mWantedOutput);
				mAdjustingOutput.setIdle();
				break;
			case ADJUSTING_LEFT:
				mOutput.setIdle();
				mAdjustingOutput.setPercentOutput(-mConfig.adjustingOutput);
				break;
			case ADJUSTING_RIGHT:
				mOutput.setIdle();
				mAdjustingOutput.setPercentOutput(mConfig.adjustingOutput);
				break;
			case IDLE:
				mOutput.setIdle();
				break;
		}
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public ControllerOutput getAdjustingOutput() {
		return mAdjustingOutput;
	}
}
