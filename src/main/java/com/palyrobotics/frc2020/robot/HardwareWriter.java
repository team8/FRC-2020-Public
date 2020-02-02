package com.palyrobotics.frc2020.robot;

import java.util.List;
import java.util.Set;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Spark;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

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
	private boolean mRumbleOutput;

	void configureHardware(Set<SubsystemBase> enabledSubsystems) {
		if (enabledSubsystems.contains(mClimber)) configureClimberHardware();
		if (enabledSubsystems.contains(mDrive)) configureDriveHardware();
		if (enabledSubsystems.contains(mIndexer)) configureIndexerHardware();
		if (enabledSubsystems.contains(mIntake)) configureIntakeHardware();
		if (enabledSubsystems.contains(mShooter)) configureShooterHardware();
		if (enabledSubsystems.contains(mSpinner)) configureSpinnerHardware();
	}

	private void configureClimberHardware() {
		var climberHardware = HardwareAdapter.ClimberHardware.getInstance();
		var config = Configs.get(ClimberConfig.class);
		climberHardware.verticalSpark.restoreFactoryDefaults();
		climberHardware.horizontalSpark.restoreFactoryDefaults();
		climberHardware.verticalSpark.enableVoltageCompensation(12);
		climberHardware.verticalSpark.getEncoder().setPositionConversionFactor(config.positionConversionFactor);
		climberHardware.verticalSpark.getEncoder().setVelocityConversionFactor(config.velocityConversionFactor);
		climberHardware.verticalSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		climberHardware.horizontalSpark.setIdleMode(CANSparkMax.IdleMode.kCoast);

	}

	private void configureDriveHardware() {
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		var driveConfig = Configs.get(DriveConfig.class);
		for (Spark spark : driveHardware.sparks) {
			spark.restoreFactoryDefaults();
			spark.enableVoltageCompensation(DriveConstants.kMaxVoltage);
			CANEncoder encoder = spark.getEncoder();
			encoder.setPositionConversionFactor(DriveConstants.kDriveMetersPerRotation);
			encoder.setVelocityConversionFactor(DriveConstants.kDriveMetersPerSecondPerRpm);
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
	}

	// private void configureDriveHardware() {
	// var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
	// var driveConfig = Configs.get(DriveConfig.class);
	// for (Falcon falcon : driveHardware.falcons) {
	// falcon.configFactoryDefault();
	// falcon.enableVoltageCompensation(true);
	// falcon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,
	// kPidIndex, kTimeoutMs);
	// falcon.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero,
	// kTimeoutMs);
	// falcon.configOpenloopRamp(driveConfig.controllerRampRate, kTimeoutMs);
	// falcon.configClosedloopRamp(driveConfig.controllerRampRate, kTimeoutMs);
	// falcon.configSensorConversions(driveConfig.positionConversion,
	// driveConfig.velocityConversion);
	// }
	//
	// /* Left Side */
	// driveHardware.leftMasterFalcon.setInverted(false);
	// driveHardware.leftSlaveFalcon.setInverted(false);
	// driveHardware.leftSlaveFalcon.follow(driveHardware.leftMasterFalcon);
	//
	// /* Right Side */
	// driveHardware.rightMasterFalcon.setInverted(false);
	// driveHardware.rightSlaveFalcon.setInverted(false);
	// driveHardware.rightSlaveFalcon.follow(driveHardware.rightMasterFalcon);
	//
	// resetDriveSensors(new Pose2d());
	// }

	private void configureIndexerHardware() {
		var indexerHardware = HardwareAdapter.IndexerHardware.getInstance();
		indexerHardware.horizontalSpark.restoreFactoryDefaults();
		indexerHardware.verticalSpark.restoreFactoryDefaults();
		indexerHardware.verticalSpark.follow(indexerHardware.horizontalSpark);
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

	// public void resetDriveSensors(Pose2d pose) {
	// var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
	// driveHardware.gyro.setYaw(pose.getRotation().getDegrees(), kTimeoutMs);
	// driveHardware.falcons.forEach(falcon -> falcon.setSelectedSensorPosition(0,
	// kPidIndex, kTimeoutMs));
	// Log.info(kLoggerTag, String.format("Drive sensors reset to %s", pose));
	// }

	public void resetDriveSensors(Pose2d pose) {
		double heading = pose.getRotation().getDegrees();
		var driveHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		driveHardware.gyro.setYaw(heading, kTimeoutMs);
		driveHardware.leftMasterEncoder.setPosition(0.0);
		driveHardware.rightMasterEncoder.setPosition(0.0);
		Log.info(kLoggerTag, String.format("Drive Sensors Reset to %s", heading));
	}

	void setDriveNeutralMode(NeutralMode neutralMode) {
		// HardwareAdapter.DrivetrainHardware.getInstance().falcons.forEach(spark ->
		// spark.setNeutralMode(neutralMode));
		HardwareAdapter.DrivetrainHardware.getInstance().sparks.forEach(spark -> spark.setIdleMode(
				neutralMode == NeutralMode.Brake ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast));
	}

	/**
	 * Updates the hardware to run with output values of {@link SubsystemBase}'s.
	 */
	void updateHardware(Set<SubsystemBase> enabledSubsystems) {
		mRumbleOutput = false;
		if (!mRobotConfig.disableHardwareUpdates) {
			if (enabledSubsystems.contains(mClimber)) updateClimber();
			if (enabledSubsystems.contains(mDrive)) updateDrivetrain();
			if (enabledSubsystems.contains(mIndexer)) updateIndexer();
			if (enabledSubsystems.contains(mClimber)) updateIntake();
			if (enabledSubsystems.contains(mShooter)) updateShooter();
			if (enabledSubsystems.contains(mSpinner)) updateSpinner();
			updateMiscellaneousHardware();
		}
		var joystickHardware = HardwareAdapter.Joysticks.getInstance();
		joystickHardware.operatorXboxController.setRumble(mRumbleOutput);
	}

	private void updateClimber() {
		var climberHardware = HardwareAdapter.ClimberHardware.getInstance();
		climberHardware.verticalSpark.setOutput(mClimber.getVerticalOutput());
		climberHardware.horizontalSpark.setOutput(mClimber.getAdjustingOutput());
		climberHardware.solenoid.set(mClimber.getSolenoidOutput());
	}

	// private void updateDrivetrain() {
	// var drivetrainHardware = HardwareAdapter.DrivetrainHardware.getInstance();
	// drivetrainHardware.leftMasterFalcon.setOutput(mDrive.getDriveSignal().leftOutput);
	// drivetrainHardware.rightMasterFalcon.setOutput(mDrive.getDriveSignal().rightOutput);
	// }

	private void updateDrivetrain() {
		var drivetrainHardware = HardwareAdapter.DrivetrainHardware.getInstance();
		drivetrainHardware.leftMasterSpark.setOutput(mDrive.getDriveSignal().leftOutput);
		drivetrainHardware.rightMasterSpark.setOutput(mDrive.getDriveSignal().rightOutput);
	}

	private void updateIndexer() {
		var indexerHardware = HardwareAdapter.IndexerHardware.getInstance();
		indexerHardware.horizontalSpark.setOutput(mIndexer.getOutput());
		indexerHardware.hopperSolenoid.set(mIndexer.getHopperOutput());
		indexerHardware.blockingSolenoid.set(mIndexer.getBlockOutput());
	}

	private void updateIntake() {
		var intakeHardware = HardwareAdapter.IntakeHardware.getInstance();
		intakeHardware.talon.setOutput(mIntake.getOutput());
		intakeHardware.upDownSolenoid.set(mIntake.getUpDownOutput());
	}

	private void updateShooter() {
		var hardware = HardwareAdapter.ShooterHardware.getInstance();
		hardware.masterSpark.setOutput(mShooter.getFlywheelOutput());
		hardware.blockingSolenoid.set(mShooter.getBlockingOutput());
		hardware.hoodSolenoid.set(mShooter.getHoodOutput());
		mRumbleOutput |= mShooter.getRumbleOutput();
	}

	private void updateSpinner() {
		var spinnerHardware = HardwareAdapter.SpinnerHardware.getInstance();
		spinnerHardware.talon.setOutput(mSpinner.getOutput());
	}

	private void updateMiscellaneousHardware() {
		HardwareAdapter.MiscellaneousHardware.getInstance();
	}
}
