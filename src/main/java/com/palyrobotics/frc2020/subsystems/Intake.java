package com.palyrobotics.frc2020.subsystems;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.palyrobotics.frc2020.robot.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Intake extends SubsystemBase {

	public static final int kTimeoutMs = 150;
	public static final double kVoltageCompensation = 12.0;
	public static final SupplyCurrentLimitConfiguration k30AmpCurrentLimitConfiguration = new SupplyCurrentLimitConfiguration(
			true, 30.0, 35.0, 1.0);

	public enum State {
		STOW, LOWER, INTAKE
	}

	public void configureIntakeHardware() {
		var talon = HardwareAdapter.IntakeHardware.getInstance().talon;
		talon.configFactoryDefault(kTimeoutMs);
		talon.setInverted(true);
		talon.enableVoltageCompensation(true);
		talon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		talon.configOpenloopRamp(0.1, kTimeoutMs);
		talon.configSupplyCurrentLimit(k30AmpCurrentLimitConfiguration, kTimeoutMs);
		talon.configFrameTimings(40, 40);
	}

	private static Intake sInstance = new Intake();
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean mExtendedOutput;

	private Intake() {
	}

	public static Intake getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
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
				mOutput.setPercentOutput(commands.intakeWantedPercentOutput);
				mExtendedOutput = true;
				break;
		}
	}

	public void updateIntake() {
		var hardware = HardwareAdapter.IntakeHardware.getInstance();
		hardware.talon.handleReset();
		hardware.talon.setOutput(getOutput());
		hardware.solenoid.setExtended(getExtendedOutput());
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getExtendedOutput() {
		return mExtendedOutput;
	}
}
