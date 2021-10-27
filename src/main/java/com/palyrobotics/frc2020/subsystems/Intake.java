package com.palyrobotics.frc2020.subsystems;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.robot.*;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.control.TimedSolenoid;

public class Intake extends SubsystemBase {

	public static final int kTimeoutMs = 150;
	public static final double kVoltageCompensation = 12.0;
	public static final SupplyCurrentLimitConfiguration k30AmpCurrentLimitConfiguration = new SupplyCurrentLimitConfiguration(
			true, 30.0, 35.0, 1.0);
	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	public enum State {
		STOW, LOWER, INTAKE
	}

	public final Talon talon = new Talon(sPortConstants.nariIntakeId, "Intake");
	public final TimedSolenoid solenoid = new TimedSolenoid(sPortConstants.nariIntakeSolenoidId, 1.0, false);

	public void configureIntakeHardware() {
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
		talon.handleReset();
		talon.setOutput(getOutput());
		solenoid.setExtended(getExtendedOutput());
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getExtendedOutput() {
		return mExtendedOutput;
	}
}
