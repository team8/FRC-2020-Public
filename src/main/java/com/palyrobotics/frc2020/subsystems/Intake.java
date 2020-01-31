package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Intake extends SubsystemBase {

	public enum IntakeState {
		IDLE, RAISE, INTAKE
	}

	private static Intake sInstance = new Intake();
	private IntakeConfig mConfig = Configs.get(IntakeConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean mUpDownOutput;

	private Intake() {
	}

	public static Intake getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		IntakeState state = commands.intakeWantedState;
		switch (state) {
			case IDLE:
			case RAISE:
				mOutput.setIdle();
				mUpDownOutput = false;
				break;
			case INTAKE:
				mOutput.setTargetVelocityProfiled(mConfig.intakingVelocity, mConfig.profiledVelocityGains);
				mUpDownOutput = true;
				break;
		}
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getUpDownOutput() {
		return mUpDownOutput;
	}
}
