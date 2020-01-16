package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.StringUtil;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Spark;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

public class HardwareWriter {

	public static final int TIMEOUT_MS = 500;
	private static final String LOGGER_TAG = StringUtil.classToJsonName(HardwareWriter.class);
	private static HardwareWriter sInstance = new HardwareWriter();
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	private final Drive mDrive = Drive.getInstance();
	private final Spinner mSpinner = Spinner.getInstance();
	private final Intake mIntake = Intake.getInstance();

	private HardwareWriter() {
	}

	public static HardwareWriter getInstance() {
		return sInstance;
	}

	void configureHardware() {
		configureDriveHardware();
		configureIntakeHardware();
		configureSpinnerHardware();
	}

	private void configureDriveHardware() {
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();

		var driveConfig = Configs.get(DriveConfig.class);
		for (Spark spark : driveHardware.sparks) {
			spark.restoreFactoryDefaults();
			spark.enableVoltageCompensation(DrivetrainConstants.kMaxVoltage);
			CANEncoder encoder = spark.getEncoder();
			encoder.setPositionConversionFactor(DrivetrainConstants.kDriveMetersPerRotation);
			encoder.setVelocityConversionFactor(DrivetrainConstants.kDriveMetersPerSecondPerRpm);
			spark.setSmartCurrentLimit(driveConfig.stallCurrentLimit, driveConfig.freeCurrentLimit,
					driveConfig.freeRpmLimit);
			spark.setOpenLoopRampRate(driveConfig.controllerRampRate);
			spark.setClosedLoopRampRate(driveConfig.controllerRampRate);
		}

		/* Left Side */
		for (Spark spark : List.of(driveHardware.leftMasterSpark, driveHardware.leftSlave1Spark,
				driveHardware.leftSlave2Spark)) {
			spark.setInverted(false);
		}
		for (Spark spark : List.of(driveHardware.leftSlave1Spark, driveHardware.leftSlave2Spark)) {
			spark.follow(driveHardware.leftMasterSpark);
		}

		/* Right Side */
		for (Spark spark : List.of(driveHardware.rightMasterSpark, driveHardware.rightSlave1Spark,
				driveHardware.rightSlave2Spark)) {
			spark.setInverted(true); // Note: Inverted
		}
		for (Spark spark : List.of(driveHardware.rightSlave1Spark, driveHardware.rightSlave2Spark)) {
			spark.follow(driveHardware.rightMasterSpark);
		}

		resetDriveSensors();
	}

	private void configureIntakeHardware() {
		var intakeHardware = HardwareAdapter.IntakeHardware.getInstance();
		intakeHardware.intakeVictor.setInverted(false);
	}

	private void configureSpinnerHardware() {
	}

	public void resetDriveSensors() {
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		driveHardware.gyro.setYaw(0, TIMEOUT_MS);
		driveHardware.gyro.setFusedHeading(0, TIMEOUT_MS);
		driveHardware.gyro.setAccumZAngle(0, TIMEOUT_MS);
		driveHardware.sparks.forEach(spark -> spark.getEncoder().setPosition(0.0));
		Log.info("Drive Sensors Reset");
	}

	void setDriveIdleMode(CANSparkMax.IdleMode idleMode) {
		HardwareAdapter.DrivetrainHardware.getInstance().sparks.forEach(spark -> spark.setIdleMode(idleMode));
	}

	/**
	 * Updates the hardware to run with output values of {@link Subsystem}'s.
	 */
	void updateHardware() {
		if (!mRobotConfig.disableOutput) {
			updateDrivetrain();
			updateSpinner();
			updateIntake();
			updateMiscellaneousHardware();
		}
	}

	private void updateDrivetrain() {
		HardwareAdapter.DrivetrainHardware.getInstance().leftMasterSpark.setOutput(mDrive.getDriveSignal().leftOutput);
		HardwareAdapter.DrivetrainHardware.getInstance().rightMasterSpark
				.setOutput(mDrive.getDriveSignal().rightOutput);
	}

	private void updateSpinner() {
		HardwareAdapter.SpinnerHardware.getInstance().spinnerTalon.set(ControlMode.PercentOutput, mSpinner.getOutput());
	}

	private void updateIntake() {
		HardwareAdapter.IntakeHardware.getInstance().intakeVictor.set(mIntake.getOutput());
	}

	private void updateMiscellaneousHardware() {
	}
}