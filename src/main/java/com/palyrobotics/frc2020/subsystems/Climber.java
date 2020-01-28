package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Climber extends SubsystemBase {

	public enum ClimberState {
		RAISING, LOWERING_TO_BAR, CLIMBING, ADJUSTING, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mClimbingOutput = new ControllerOutput(), mAdjustingOutput = new ControllerOutput();
	private boolean mSolenoidOutput = false;
	private ClimberState mState = ClimberState.IDLE;
	private ClimberConfig mConfig = Configs.get(ClimberConfig.class);

	private Climber() {
	}

	public static Climber getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		mState = commands.climberWantedState;
		switch (mState) {
			case RAISING:
				mClimbingOutput.setTargetPositionProfiled(mConfig.climberTopHeight, mConfig.raisingArbitraryDemand,
						mConfig.raisingGains);
				mAdjustingOutput.setIdle();
				mSolenoidOutput = true;
				break;
			case LOWERING_TO_BAR:
				mClimbingOutput.setPercentOutput(mConfig.loweringPercentOutput);
				mAdjustingOutput.setIdle();
				mSolenoidOutput = true;
				break;
			case CLIMBING:
				mClimbingOutput.setPercentOutput(mConfig.climbingPercentOutput);
				mAdjustingOutput.setIdle();
				mSolenoidOutput = true;
				break;
			case ADJUSTING:
				mClimbingOutput.setIdle();
				mAdjustingOutput.setPercentOutput(commands.climberWantedAdjustingPercentOutput);
				mSolenoidOutput = false;
				break;
			case IDLE:
				mClimbingOutput.setIdle();
				mAdjustingOutput.setIdle();
				mSolenoidOutput = false;
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
