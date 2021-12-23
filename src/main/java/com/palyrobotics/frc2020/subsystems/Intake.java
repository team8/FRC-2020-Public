package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.robot.HardwareWriter.*;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.HardwareAdapter;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Intake extends SubsystemBase {

	public enum State {
		STOW, LOWER, INTAKE
	}

	private static Intake sInstance = new Intake();
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean mExtendedOutput;
	private HardwareAdapter.IntakeHardware hardware = HardwareAdapter.IntakeHardware.getInstance();

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

	@Override
	public void writeHardware(RobotState state) {
		hardware.talon.handleReset();
		hardware.talon.setOutput(mOutput);
		hardware.solenoid.setExtended(mExtendedOutput);
	}

	@Override
	public void configureHardware() {
		hardware.talon.configFactoryDefault(kTimeoutMs);
		hardware.talon.setInverted(true);
		hardware.talon.enableVoltageCompensation(true);
		hardware.talon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		hardware.talon.configOpenloopRamp(0.1, kTimeoutMs);
		hardware.talon.configSupplyCurrentLimit(k30AmpCurrentLimitConfiguration, kTimeoutMs);
		hardware.talon.configFrameTimings(40, 40);
	}
}
