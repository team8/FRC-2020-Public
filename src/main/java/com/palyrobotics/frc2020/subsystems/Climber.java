package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.robot.*;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.TimedSolenoid;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

public class Climber extends SubsystemBase {

	public static final double kVoltageCompensation = 12.0;
	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	public enum State {
		MANUAL, LOCKED, IDLE
	}

	private final Spark spark = new Spark(sPortConstants.nariClimberId, "Climber");
	private final CANEncoder sparkEncoder = spark.getEncoder();
	private final TimedSolenoid solenoid = new TimedSolenoid(sPortConstants.nariClimberSolenoidId, 0.2, true);

	public void configureClimberHardware() {
		spark.restoreFactoryDefaults();
		spark.enableVoltageCompensation(kVoltageCompensation);
		/* Encoder units are inches and inches/sec */
		sparkEncoder.setPositionConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		sparkEncoder.setVelocityConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		spark.setInverted(true);
		sparkEncoder.setPosition(0.0);
		spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, 160.0f);
		spark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, 0.0f);
		spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
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
		spark.setOutput(getControllerOutput());
		solenoid.setExtended(getSolenoidOutput());
	}

	public ControllerOutput getControllerOutput() {
		return mControllerOutput;
	}

	public boolean getSolenoidOutput() {
		return mSolenoidOutput;
	}

	public void setClimberSoftLimitsEnabled(boolean isEnabled) {
		spark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, isEnabled);
		spark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, isEnabled);
	}
}
