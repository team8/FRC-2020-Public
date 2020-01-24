package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Climber extends SubsystemBase {

	public enum ClimberState {
		CLIMBING, ADJUSTING_LEFT, ADJUSTING_RIGHT, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mClimbingOutput = new ControllerOutput(), mAdjustingOutput = new ControllerOutput();
	private ClimberConfig mConfig = Configs.get(ClimberConfig.class);

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
				double wantedOutput = commands.getClimberWantedOutput();
				mClimbingOutput.setPercentOutput(wantedOutput);
				mAdjustingOutput.setIdle();
				break;
			case ADJUSTING_LEFT:
				mClimbingOutput.setIdle();
				mAdjustingOutput.setPercentOutput(-mConfig.adjustingOutput);
				break;
			case ADJUSTING_RIGHT:
				mClimbingOutput.setIdle();
				mAdjustingOutput.setPercentOutput(mConfig.adjustingOutput);
				break;
			case IDLE:
				mClimbingOutput.setIdle();
				mAdjustingOutput.setIdle();
				break;
		}
	}

	public ControllerOutput getClimbingOutput() {
		return mClimbingOutput;
	}

	public ControllerOutput getAdjustingOutput() {
		return mAdjustingOutput;
	}
}
