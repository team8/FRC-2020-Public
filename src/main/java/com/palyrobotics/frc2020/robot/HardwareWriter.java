package com.palyrobotics.frc2020.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;

import edu.wpi.first.wpilibj.geometry.Pose2d;

public class HardwareWriter {

	public static final int kTimeoutMs = 50,
			// Different from slot index.
			// 0 for Primary closed-loop. 1 for auxiliary closed-loop.
			kPidIndex = 0;
	private static final String kLoggerTag = Util.classToJsonName(HardwareWriter.class);
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	private final Climber mClimber = Climber.getInstance();
	private final Drive mDrive = Drive.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Intake mIntake = Intake.getInstance();
	private final Shooter mShooter = Shooter.getInstance();
	private final Spinner mSpinner = Spinner.getInstance();

	void configureHardware() {
		configureClimberHardware();
		configureDriveHardware();
		configureIndexerHardware();
		configureIntakeHardware();
		configureShooterHardware();
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
			falcon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, kPidIndex, kTimeoutMs);
			falcon.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero, kTimeoutMs);
			falcon.configOpenloopRamp(driveConfig.controllerRampRate, kTimeoutMs);
			falcon.configClosedloopRamp(driveConfig.controllerRampRate, kTimeoutMs);
			falcon.configSensorConversions(driveConfig.positionConversion, driveConfig.velocityConversion);
		}

		/* Left Side */
		driveHardware.leftMasterFalcon.setInverted(false);
		driveHardware.leftSlaveFalcon.setInverted(false);
		driveHardware.leftSlaveFalcon.follow(driveHardware.leftMasterFalcon);

		/* Right Side */
		driveHardware.rightMasterFalcon.setInverted(false);
		driveHardware.rightSlaveFalcon.setInverted(false);
		driveHardware.rightSlaveFalcon.follow(driveHardware.rightMasterFalcon);

		resetDriveSensors(new Pose2d());
	}

	private void configureIndexerHardware() {
		var indexerHardware = HardwareAdapter.IndexerHardware.getInstance();
		indexerHardware.horizontalSpark.restoreFactoryDefaults();
		indexerHardware.verticalSpark.restoreFactoryDefaults();

		indexerHardware.backUltrasonic.setAutomaticMode(true);
		indexerHardware.frontUltrasonic.setAutomaticMode(true);

		indexerHardware.backUltrasonic.setEnabled(true);
		indexerHardware.frontUltrasonic.setEnabled(true);
	}

	private void configureIntakeHardware() {
		var intakeHardware = HardwareAdapter.IntakeHardware.getInstance();
		intakeHardware.talon.configFactoryDefault(kTimeoutMs);
	}

	private void configureShooterHardware() {
		var shooterHardware = HardwareAdapter.ShooterHardware.getInstance();
		shooterHardware.masterSpark.restoreFactoryDefaults();
		shooterHardware.slaveSpark.follow(shooterHardware.masterSpark);
		// TODO: Add velocity conversions and other configs
	}

	private void configureSpinnerHardware() {
		var spinnerHardware = HardwareAdapter.SpinnerHardware.getInstance();
		spinnerHardware.talon.configFactoryDefault(kTimeoutMs);
	}

	public void resetDriveSensors(Pose2d pose) {
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		driveHardware.gyro.setYaw(0.0, kTimeoutMs);
		driveHardware.gyro.setFusedHeading(pose.getRotation().getDegrees(), kTimeoutMs);
		driveHardware.gyro.setAccumZAngle(0.0, kTimeoutMs);
		driveHardware.falcons.forEach(spark -> spark.setSelectedSensorPosition(0, kPidIndex, kTimeoutMs));
		Log.info(kLoggerTag, "Drive sensors reset");
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
			updateShooter();
			updateSpinner();
			updateMiscellaneousHardware();
		}
	}

	private void updateClimber() {
		var climberHardware = HardwareAdapter.ClimberHardware.getInstance();
		climberHardware.verticalSpark.setOutput(mClimber.getClimbingOutput());
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
		var intakeHardware = HardwareAdapter.IntakeHardware.getInstance();
		intakeHardware.talon.setOutput(mIntake.getOutput());
		intakeHardware.upDownSolenoid.set(mIntake.getUpDownOutput());
	}

	private void updateShooter() {
		var shooterHardware = HardwareAdapter.ShooterHardware.getInstance();
		shooterHardware.masterSpark.setOutput(mShooter.getFlywheelOutput());
		shooterHardware.blockingSolenoid.set(mShooter.getBlockingOutput());
		shooterHardware.hoodSolenoid.set(mShooter.getHoodOutput());
	}

	private void updateSpinner() {
		var spinnerHardware = HardwareAdapter.SpinnerHardware.getInstance();
		spinnerHardware.talon.setOutput(mSpinner.getOutput());
	}

	private void updateMiscellaneousHardware() {
	}
}
