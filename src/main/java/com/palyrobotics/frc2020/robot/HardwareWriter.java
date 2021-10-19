package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.SoftLimitDirection;

import edu.wpi.first.wpilibj.geometry.Pose2d;

public class HardwareWriter {

	public static final int
	// Blocks config calls for specified timeout
	kTimeoutMs = 150,
			// Different from slot index.
			// 0 for Primary closed-loop. 1 for auxiliary closed-loop.
			kPidIndex = 0;
	private static final String kLoggerTag = Util.classToJsonName(HardwareWriter.class);
	public static final double kVoltageCompensation = 12.0;
	public static final SupplyCurrentLimitConfiguration k30AmpCurrentLimitConfiguration = new SupplyCurrentLimitConfiguration(
			true, 30.0, 35.0, 1.0);
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	private final IndexerConfig mIndexerConfig = Configs.get(IndexerConfig.class);
	private final Climber mClimber = Climber.getInstance();
	private final Drive mDrive = Drive.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Intake mIntake = Intake.getInstance();
	private final Lighting mLighting = Lighting.getInstance();
	private final Shooter mShooter = Shooter.getInstance();
	private final Spinner mSpinner = Spinner.getInstance();
	private boolean mRumbleOutput;

	void configureHardware(Set<SubsystemBase> enabledSubsystems) {
		if (enabledSubsystems.contains(mClimber)) configureClimberHardware();
		if (enabledSubsystems.contains(mDrive)) configureDriveHardware();
		if (enabledSubsystems.contains(mIndexer)) configureIndexerHardware();
		if (enabledSubsystems.contains(mIntake)) configureIntakeHardware();
		if (enabledSubsystems.contains(mLighting)) configureLightingHardware();
		if (enabledSubsystems.contains(mShooter)) configureShooterHardware();
		if (enabledSubsystems.contains(mSpinner)) configureSpinnerHardware();
		configureMiscellaneousHardware();
	}

	private void configureMiscellaneousHardware() {
		var hardware = HardwareAdapter.MiscellaneousHardware.getInstance();
		hardware.pdp.clearStickyFaults();
		hardware.compressor.clearAllPCMStickyFaults();
	}

	private void configureClimberHardware() {
		var hardware = HardwareAdapter.ClimberHardware.getInstance();
		hardware.spark.restoreFactoryDefaults();
		hardware.spark.enableVoltageCompensation(kVoltageCompensation);
		/* Encoder units are inches and inches/sec */
		hardware.sparkEncoder.setPositionConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		hardware.sparkEncoder.setVelocityConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		hardware.spark.setInverted(true);
		hardware.sparkEncoder.setPosition(0.0);
		hardware.spark.setSoftLimit(SoftLimitDirection.kForward, 160.0f);
		hardware.spark.setSoftLimit(SoftLimitDirection.kReverse, 0.0f);
		hardware.spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
	}

	private void configureDriveHardware() {
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		/* Falcons */
		for (Falcon falcon : hardware.falcons) {
			falcon.configFactoryDefault(kTimeoutMs);
			falcon.enableVoltageCompensation(true);
			falcon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
			falcon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, kPidIndex, kTimeoutMs);
			falcon.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero, kTimeoutMs);
			falcon.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(
					true, 40.0, 50.0, 1.0));
			falcon.configOpenloopRamp(0.1, kTimeoutMs);
			falcon.configClosedloopRamp(0.1, kTimeoutMs);
			falcon.configSensorConversions(DriveConstants.kDriveMetersPerTick, DriveConstants.kDriveMetersPerSecondPerTickPer100Ms);
		}
		// Left
		hardware.leftMasterFalcon.setInverted(false);
		hardware.leftMasterFalcon.setFrameTimings(5, 5);
		hardware.leftSlaveFalcon.follow(hardware.leftMasterFalcon);
		hardware.leftSlaveFalcon.setInverted(InvertType.FollowMaster);
		hardware.leftSlaveFalcon.setFrameTimings(40, 40);
		// Right
		hardware.rightMasterFalcon.setInverted(true);
		hardware.rightMasterFalcon.setFrameTimings(5, 5);
		hardware.rightSlaveFalcon.follow(hardware.rightMasterFalcon);
		hardware.rightSlaveFalcon.setInverted(InvertType.FollowMaster);
		hardware.rightSlaveFalcon.setFrameTimings(40, 40);
		/* Gyro */
		// 10 ms update period for yaw degrees and yaw angular velocity in degrees per second
		setPigeonStatusFramePeriods(hardware.gyro);
		/* Falcons and Gyro */
		resetDriveSensors(new Pose2d());
	}

	private void configureIndexerHardware() {
		var hardware = HardwareAdapter.IndexerHardware.getInstance();
		for (Spark spark : hardware.columnSparks) {
			spark.restoreFactoryDefaults();
			spark.enableVoltageCompensation(kVoltageCompensation);
			spark.setSmartCurrentLimit(mIndexerConfig.columnStallCurrentLimit, mIndexerConfig.columnFreeCurrentLimit);
			spark.setOpenLoopRampRate(mIndexerConfig.rampRate);
			spark.setInverted(true);
		}
		hardware.masterColumnSparkEncoder.setPosition(0);
		hardware.slaveColumnSparkEncoder.setPosition(0);
		for (Talon talon : hardware.vTalons) {
			talon.configFactoryDefault(kTimeoutMs);
			SupplyCurrentLimitConfiguration vTalonSupplyCurrentLimit = new SupplyCurrentLimitConfiguration(true, mIndexerConfig.vTalonCurrentLimit, 0, kTimeoutMs / 1000.0);
			talon.configVoltageCompSaturation(kVoltageCompensation);
			talon.configSupplyCurrentLimit(vTalonSupplyCurrentLimit);
			talon.setInverted(true);
		}
	}

	private void configureIntakeHardware() {
		var talon = HardwareAdapter.IntakeHardware.getInstance().talon;
		talon.configFactoryDefault(kTimeoutMs);
		talon.setInverted(true);
		talon.enableVoltageCompensation(true);
		talon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		talon.configOpenloopRamp(0.1, kTimeoutMs);
		talon.configSupplyCurrentLimit(k30AmpCurrentLimitConfiguration, kTimeoutMs);
		talon.configFrameTimings(40, 40);
	}

	private void configureLightingHardware() {
		var hardware = HardwareAdapter.LightingHardware.getInstance();
		hardware.ledStrip.setLength(Configs.get(LightingConfig.class).ledCount);
		hardware.ledStrip.start();
		hardware.ledStrip.setData(mLighting.getOutput());
	}

	private void configureShooterHardware() {
		var hardware = HardwareAdapter.ShooterHardware.getInstance();
		hardware.masterSpark.restoreFactoryDefaults();
		hardware.slaveSpark.restoreFactoryDefaults();
		hardware.slaveSpark.follow(hardware.masterSpark, true);
		hardware.masterSpark.setInverted(true);
		/* Flywheel velocity in RPM, adjusted for gearing ratio */
		hardware.masterEncoder.setVelocityConversionFactor(1.0 / 0.76923076);
		// TODO: Current limiting and closed/open loop ramp rates
	}

	private void configureSpinnerHardware() {
		var talon = HardwareAdapter.SpinnerHardware.getInstance().talon;
		talon.configFactoryDefault(kTimeoutMs);
		talon.configOpenloopRamp(0.1, kTimeoutMs);
		talon.enableVoltageCompensation(true);
		talon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		talon.setNeutralMode(NeutralMode.Brake);
	}

	public void resetDriveSensors(Pose2d pose) {
		double heading = pose.getRotation().getDegrees();
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		hardware.gyro.setYaw(heading, kTimeoutMs);
		hardware.leftMasterFalcon.setSelectedSensorPosition(0);
		hardware.rightMasterFalcon.setSelectedSensorPosition(0);
		Log.info(kLoggerTag, String.format("Drive sensors reset, gyro heading: %s", heading));
	}

	void setDriveNeutralMode(NeutralMode neutralMode) {
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		hardware.leftMasterFalcon.setNeutralMode(neutralMode);
		hardware.rightMasterFalcon.setNeutralMode(neutralMode);
	}

	void setClimberSoftLimitsEnabled(boolean isEnabled) {
		var spark = HardwareAdapter.ClimberHardware.getInstance().spark;
		spark.enableSoftLimit(SoftLimitDirection.kForward, isEnabled);
		spark.enableSoftLimit(SoftLimitDirection.kReverse, isEnabled);
	}

	/**
	 * Updates the hardware to run with output values of {@link SubsystemBase}'s.
	 */
	void updateHardware(Set<SubsystemBase> enabledSubsystems, @ReadOnly RobotState robotState) {
		mRumbleOutput = false;
		if (!mRobotConfig.disableHardwareUpdates) {
			if (enabledSubsystems.contains(mClimber)) updateClimber();
			if (enabledSubsystems.contains(mDrive)) updateDrivetrain();
//			if (enabledSubsystems.contains(mDrive) && robotState.gamePeriod != GamePeriod.TESTING) updateDrivetrain();
			if (enabledSubsystems.contains(mIndexer)) updateIndexer();
			if (enabledSubsystems.contains(mIntake)) updateIntake();
			if (enabledSubsystems.contains(mShooter)) updateShooter();
			if (enabledSubsystems.contains(mSpinner)) updateSpinner();
			if (enabledSubsystems.contains(mLighting)) updateLighting();
		}
		var joystickHardware = HardwareAdapter.Joysticks.getInstance();
		joystickHardware.operatorXboxController.setRumble(mRumbleOutput);
	}

	private void updateClimber() {
		var hardware = HardwareAdapter.ClimberHardware.getInstance();
		hardware.spark.setOutput(mClimber.getControllerOutput());
		hardware.solenoid.setExtended(mClimber.getSolenoidOutput());
	}

	private void updateDrivetrain() {
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		hardware.falcons.forEach(Falcon::handleReset);
		hardware.leftMasterFalcon.setOutput(mDrive.getDriveSignal().leftOutput);
		hardware.rightMasterFalcon.setOutput(mDrive.getDriveSignal().rightOutput);
		handleReset(hardware.gyro);
	}

	private void updateIndexer() {
		var hardware = HardwareAdapter.IndexerHardware.getInstance();
		hardware.masterColumnSpark.setOutput(mIndexer.getMasterIndexerColumnOutput());
		hardware.slaveColumnSpark.setOutput(mIndexer.getSlaveIndexerColumnOutput());
		hardware.leftVTalon.setOutput(mIndexer.getLeftVTalonOutput());
		hardware.rightVTalon.setOutput(mIndexer.getRightVTalonOutput());
		hardware.blockingSolenoid.set(mIndexer.getBlockingSolenoidOutput());
		hardware.hopperSolenoids.set(mIndexer.getHopperSolenoidOutput());
	}

	private void updateIntake() {
		var hardware = HardwareAdapter.IntakeHardware.getInstance();
		hardware.talon.handleReset();
		hardware.talon.setOutput(mIntake.getOutput());
		hardware.solenoid.setExtended(mIntake.getSolenoidOutput());
	}

	public void updateLighting() {
		var hardware = HardwareAdapter.LightingHardware.getInstance();
		hardware.ledStrip.setData(mLighting.getOutput());
	}

	private void updateShooter() {
		var hardware = HardwareAdapter.ShooterHardware.getInstance();
//		handleReset(hardware.masterSpark, hardware.slaveSpark);
//		Robot.mDebugger.addPoint("handleReset");
		hardware.masterSpark.setOutput(mShooter.getFlywheelOutput());
//		Robot.mDebugger.addPoint("masterSpark.setOutput");
		hardware.blockingSolenoid.setExtended(mShooter.getBlockingOutput());
//		Robot.mDebugger.addPoint("blockingSolenoid.setOutput");
		hardware.hoodSolenoid.setExtended(mShooter.getHoodOutput());
//		Robot.mDebugger.addPoint("hoodSolenoid.setOutput");
		mRumbleOutput |= mShooter.getRumbleOutput();
	}

	private void updateSpinner() {
		var hardware = HardwareAdapter.SpinnerHardware.getInstance();
		hardware.talon.handleReset();
		hardware.talon.setOutput(mSpinner.getOutput());
	}

	private void handleReset(Spark spark) {
//		if (spark.getStickyFault(FaultID.kHasReset)) {
//			spark.clearFaults();
//			Log.error(kLoggerTag, String.format("%s spark reset", spark.getName()));
//		}
	}

	private void setPigeonStatusFramePeriods(PigeonIMU gyro) {
		gyro.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5, kTimeoutMs);
		gyro.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro, 5, kTimeoutMs);
	}

	private void handleReset(PigeonIMU pigeon) {
		if (pigeon.hasResetOccurred()) {
			Log.error(kLoggerTag, "Pigeon reset");
			setPigeonStatusFramePeriods(pigeon);
		}
	}
}
