package com.palyrobotics.frc2020.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.StringUtil;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;

public class HardwareWriter {

	public static final int TIMEOUT_MS = 50;
	private static final String LOGGER_TAG = StringUtil.classToJsonName(HardwareWriter.class);
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	private final Drive mDrive = Drive.getInstance();
	private final Climber mClimber = Climber.getInstance();
	private final Spinner mSpinner = Spinner.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Intake mIntake = Intake.getInstance();

	void configureHardware() {
		configureDriveHardware();
		configureClimberHardware();
		configureIntakeHardware();
		configureIndexerHardware();
		configureSpinnerHardware();
	}

	private void configureClimberHardware() {
		var climberHardware = HardwareAdapter.ClimberHardware.getInstance();
		climberHardware.verticalSpark.restoreFactoryDefaults();
		climberHardware.horizontalSpark.restoreFactoryDefaults();
	}

	private void configureDriveHardware() {
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		var driveConfig = Configs.get(DriveConfig.class);
		for (Falcon falcon : driveHardware.falcons) {
			falcon.configFactoryDefault();
			falcon.enableVoltageCompensation(true);
			falcon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 50);
			falcon.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero, 50);
			falcon.configOpenloopRamp(driveConfig.controllerRampRate, 50);
			falcon.configClosedloopRamp(driveConfig.controllerRampRate, 50);
		}

		/* Left Side */
		driveHardware.leftMasterFalcon.setInverted(false);
		driveHardware.leftSlaveFalcon.setInverted(false);
		driveHardware.leftSlaveFalcon.follow(driveHardware.leftMasterFalcon);

		/* Right Side */
		driveHardware.rightMasterFalcon.setInverted(false);
		driveHardware.rightSlaveFalcon.setInverted(false);
		driveHardware.rightSlaveFalcon.follow(driveHardware.rightMasterFalcon);

		resetDriveSensors();
	}

	private void configureIndexerHardware() {
		var indexerHardware = HardwareAdapter.IndexerHardware.getInstance();
		indexerHardware.horizontalSpark.restoreFactoryDefaults();
		indexerHardware.verticalSpark.restoreFactoryDefaults();
	}

	private void configureIntakeHardware() {
		var intakeHardware = HardwareAdapter.IntakeHardware.getInstance();
		intakeHardware.talon.configFactoryDefault(TIMEOUT_MS);
	}

	private void configureSpinnerHardware() {
		var spinnerHardware = HardwareAdapter.SpinnerHardware.getInstance();
		spinnerHardware.talon.configFactoryDefault(TIMEOUT_MS);
	}

	public void resetDriveSensors() {
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		driveHardware.gyro.setYaw(0, TIMEOUT_MS);
		driveHardware.gyro.setFusedHeading(0, TIMEOUT_MS);
		driveHardware.gyro.setAccumZAngle(0, TIMEOUT_MS);
		driveHardware.falcons.forEach(spark -> spark.setSelectedSensorPosition(0, 0, 50));
		Log.info(LOGGER_TAG, "Drive sensors reset");
	}

	void setDriveNeutralMode(NeutralMode neutralMode) {
		HardwareAdapter.DrivetrainHardware.getInstance().falcons.forEach(spark -> spark.setNeutralMode(neutralMode));
	}

	/**
	 * Updates the hardware to run with output values of {@link SubsystemBase}'s.
	 */
	void updateHardware() {
		if (!mRobotConfig.disableOutput) {
			updateClimber();
			updateDrivetrain();
			updateIndexer();
			updateIntake();
			updateSpinner();
			updateMiscellaneousHardware();
		}
	}

	private void updateClimber() {
		var climberHardware = HardwareAdapter.ClimberHardware.getInstance();
		climberHardware.verticalSpark.setOutput(mClimber.getOutput());
		climberHardware.horizontalSpark.setOutput(mClimber.getAdjustingOutput());
	}

	private void updateDrivetrain() {
		var drivetrainHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		drivetrainHardware.leftMasterFalcon.setOutput(mDrive.getDriveSignal().leftOutput);
		drivetrainHardware.rightMasterFalcon.setOutput(mDrive.getDriveSignal().rightOutput);
	}

	private void updateIndexer() {
		var indexerHardware = HardwareAdapter.IndexerHardware.getInstance();
		indexerHardware.horizontalSpark.setOutput(mIndexer.getHorizontalOutput());
		indexerHardware.verticalSpark.setOutput(mIndexer.getVerticalOutput());
	}

	private void updateIntake() {
		HardwareAdapter.IntakeHardware.getInstance().talon.setOutput(mIntake.getOutput());
	}

	private void updateSpinner() {
		var spinnerHardware = HardwareAdapter.SpinnerHardware.getInstance();
		spinnerHardware.talon.setOutput(mSpinner.getOutput());
	}

	private void updateMiscellaneousHardware() {
	}
}
