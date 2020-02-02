package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Intake extends SubsystemBase {

	public enum State {
		STOW, LOWER, INTAKE
	}

	private static Intake sInstance = new Intake();
	private IntakeConfig mConfig = Configs.get(IntakeConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean mExtendedOutput;

	private Intake() {
	}

	public static Intake getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		switch (commands.intakeWantedState) {
			case STOW:
				mOutput.setIdle();
				mExtendedOutput = false;
				break;
			case LOWER:
				mOutput.setIdle();
				mExtendedOutput = true;
				break;
			case INTAKE:
				mOutput.setPercentOutput(mConfig.intakingOutput);
				mExtendedOutput = true;
				break;
		}
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getExtendedOutput() {
		return mExtendedOutput;
	}
}
