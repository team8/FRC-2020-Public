package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Climber extends SubsystemBase {

	public enum State {
		MANUAL, LOCKED, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mControllerOutput = new ControllerOutput();
	private boolean mSolenoidOutput;

	private Climber() {
	}

	public static Climber getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		switch (commands.climberWantedState) {
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
