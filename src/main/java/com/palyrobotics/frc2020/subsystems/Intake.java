package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.DualSolenoid;

public class Intake extends SubsystemBase {

	public enum IntakeState {
		IDLE, RAISE, INTAKE
	}

	private static Intake sInstance = new Intake();
	private IntakeConfig mConfig = Configs.get(IntakeConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private DualSolenoid.Output mUpDownOutput = DualSolenoid.Output.REVERSE;

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
				mOutput.setIdle();
				mUpDownOutput = DualSolenoid.Output.OFF;
				break;
			case RAISE:
				mOutput.setIdle();
				mUpDownOutput = DualSolenoid.Output.REVERSE;
				break;
			case INTAKE:
				mOutput.setTargetVelocityProfiled(mConfig.intakingVelocity, mConfig.profiledVelocityGains);
				mUpDownOutput = DualSolenoid.Output.FORWARD;
				break;
		}
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public DualSolenoid.Output getUpDownOutput() {
		return mUpDownOutput;
	}
}
