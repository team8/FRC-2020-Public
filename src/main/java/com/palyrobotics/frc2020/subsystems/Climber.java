package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.revrobotics.CANSparkMax;

public class Climber extends SubsystemBase {

	public static final double kVoltageCompensation = 12.0;

	public enum State {
		MANUAL, LOCKED, IDLE
	}

	public void configureClimberHardware() {
		var hardware = HardwareAdapter.ClimberHardware.getInstance();
		hardware.spark.restoreFactoryDefaults();
		hardware.spark.enableVoltageCompensation(kVoltageCompensation);
		/* Encoder units are inches and inches/sec */
		hardware.sparkEncoder.setPositionConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		hardware.sparkEncoder.setVelocityConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		hardware.spark.setInverted(true);
		hardware.sparkEncoder.setPosition(0.0);
		hardware.spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, 160.0f);
		hardware.spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, 0.0f);
		hardware.spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
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

	public void updateClimber() {
		var hardware = HardwareAdapter.ClimberHardware.getInstance();
		hardware.spark.setOutput(getControllerOutput());
		hardware.solenoid.setExtended(getSolenoidOutput());
	}

	public ControllerOutput getControllerOutput() {
		return mControllerOutput;
	}

	public boolean getSolenoidOutput() {
		return mSolenoidOutput;
	}

	public void setClimberSoftLimitsEnabled(boolean isEnabled) {
		var spark = HardwareAdapter.ClimberHardware.getInstance().spark;
		spark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, isEnabled);
		spark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, isEnabled);
	}
}
