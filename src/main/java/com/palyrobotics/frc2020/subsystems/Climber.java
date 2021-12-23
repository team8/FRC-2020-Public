package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.robot.HardwareWriter.kVoltageCompensation;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.HardwareAdapter;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.revrobotics.CANSparkMax;

public class Climber extends SubsystemBase {

	public enum State {
		MANUAL, LOCKED, IDLE
	}

	private static Climber sInstance = new Climber();
	private ControllerOutput mControllerOutput = new ControllerOutput();
	private HardwareAdapter.ClimberHardware hardware = HardwareAdapter.ClimberHardware.getInstance();
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

	@Override
	public void writeHardware(RobotState state) {
		hardware.spark.setOutput(mControllerOutput);
		hardware.solenoid.setExtended(mSolenoidOutput);
	}

	@Override
	public void configureHardware() {
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
}
