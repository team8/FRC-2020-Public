package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Climber extends SubsystemBase {

	public enum State {
		CUSTOM_POSITIONING, MANUAL, LOCKED, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mControllerOutput = new ControllerOutput();
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
			case CUSTOM_POSITIONING:
				mControllerOutput.setTargetPositionProfiled(commands.climberPositionSetpoint, mConfig.raisingArbitraryDemand, mConfig.raisingGains);
				mSolenoidOutput = false;
				break;
			case MANUAL:
				mControllerOutput.setPercentOutput(commands.climberWantedManualPercentOutput);
				mSolenoidOutput = false;
				break;
			case LOCKED:
				mControllerOutput.setIdle();
				mSolenoidOutput = true;
				break;
			case IDLE:
				mControllerOutput.setIdle();
				mSolenoidOutput = false;
				break;
		}
	}

	public ControllerOutput getControllerOutput() {
		return mControllerOutput;
	}

	public boolean getSolenoidOutput() {
		return mSolenoidOutput;
	}
}
