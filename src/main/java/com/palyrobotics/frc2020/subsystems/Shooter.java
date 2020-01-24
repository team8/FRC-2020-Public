package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Shooter extends SubsystemBase {

	public enum ShooterState {
		IDLE, VELOCITY
	}

	public enum HoodState {
		LOW, MIDDLE, HIGH
	}

	private static Shooter sInstance = new Shooter();
	private ShooterConfig mConfig = Configs.get(ShooterConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean mExtendingOutput = false, mRetractingOutput = false, mBlockingOutput = false;

	private Shooter() {
	}

	public static Shooter getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {

	}

	public ControllerOutput getControllerOutput() {
		return mOutput;
	}

	public boolean getExtendingOutput() {
		return mExtendingOutput;
	}

	public boolean getRetractingOutput() {
		return mRetractingOutput;
	}

	public boolean getBlockingOutput() {
		return mBlockingOutput;
	}
}
