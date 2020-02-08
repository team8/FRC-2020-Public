package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Climber extends SubsystemBase {

	public enum ClimberState {
		RAISING, LOWERING_TO_BAR, CLIMBING, LOCKED, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mVerticalOutput = new ControllerOutput(), mAdjustingOutput = new ControllerOutput();
	private boolean mSolenoidOutput;
	private ClimberConfig mConfig = Configs.get(ClimberConfig.class);

	private Climber() {
	}

	public static Climber getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		switch (commands.climberWantedState) {
			case RAISING:
				mVerticalOutput.setTargetPositionProfiled(mConfig.climberTopHeight, mConfig.raisingArbitraryDemand,
						mConfig.raisingGains);
				mAdjustingOutput.setIdle();
				mSolenoidOutput = true;
				break;
			case LOWERING_TO_BAR:
				mVerticalOutput.setPercentOutput(mConfig.loweringPercentOutput);
				mAdjustingOutput.setIdle();
				mSolenoidOutput = true;
				break;
			case CLIMBING:
				mVerticalOutput.setTargetVelocityProfiled(commands.climberWantedVelocity,
						mConfig.climbingArbitraryDemand, mConfig.climbingGains);
				mAdjustingOutput.setPercentOutput(commands.climberWantedAdjustingPercentOutput);
				mSolenoidOutput = true;
				break;
			case LOCKED:
				mVerticalOutput.setIdle();
				mAdjustingOutput.setPercentOutput(commands.climberWantedAdjustingPercentOutput);
				mSolenoidOutput = false;
				break;
			case IDLE:
				mVerticalOutput.setIdle();
				mAdjustingOutput.setIdle();
				mSolenoidOutput = true;
				break;
		}
	}

	public ControllerOutput getVerticalOutput() {
		return mVerticalOutput;
	}

	public ControllerOutput getAdjustingOutput() {
		return mAdjustingOutput;
	}

	public boolean getSolenoidOutput() {
		return mSolenoidOutput;
	}
}
