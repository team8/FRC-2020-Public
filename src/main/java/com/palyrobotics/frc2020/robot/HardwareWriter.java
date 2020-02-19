package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.FaultID;

import edu.wpi.first.wpilibj.geometry.Pose2d;

public class HardwareWriter {

	public static final int
	// Blocks config calls for specified timeout
	kTimeoutMs = 100,
			// Different from slot index.
			// 0 for Primary closed-loop. 1 for auxiliary closed-loop.
			kPidIndex = 0;
	private static final String kLoggerTag = Util.classToJsonName(HardwareWriter.class);
	public static final double kVoltageCompensation = 12.0;
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
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
		hardware.verticalSpark.restoreFactoryDefaults();
		hardware.horizontalSpark.restoreFactoryDefaults();
		hardware.verticalSpark.enableVoltageCompensation(kVoltageCompensation);
		hardware.horizontalSpark.enableVoltageCompensation(kVoltageCompensation);
		/* Encoder units are inches and inches/sec */
		hardware.verticalSparkEncoder.setPositionConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		hardware.verticalSparkEncoder.setVelocityConversionFactor((1.0 / 17.0666667) * 4.0 * Math.PI);
		hardware.verticalSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		hardware.horizontalSpark.setIdleMode(CANSparkMax.IdleMode.kCoast);
	}

	private void configureDriveHardware() {
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		var driveConfig = Configs.get(DriveConfig.class);
		for (Falcon falcon : hardware.falcons) {
			falcon.configFactoryDefault(kTimeoutMs);
			falcon.enableVoltageCompensation(true);
			falcon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
			falcon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, kPidIndex, kTimeoutMs);
			falcon.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero, kTimeoutMs);
//			falcon.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration())
			falcon.configOpenloopRamp(driveConfig.controllerRampRate, kTimeoutMs);
			falcon.configClosedloopRamp(driveConfig.controllerRampRate, kTimeoutMs);
			falcon.configSensorConversions(DriveConstants.kDriveMetersPerTick, DriveConstants.kDriveMetersPerSecondPerTickPer100Ms);
		}

		/* Left Side */
		hardware.leftMasterFalcon.setInverted(false);
		hardware.leftSlaveFalcon.follow(hardware.leftMasterFalcon);
		hardware.leftSlaveFalcon.setInverted(InvertType.FollowMaster);

		/* Right Side */
		hardware.rightMasterFalcon.setInverted(true);
		hardware.rightSlaveFalcon.follow(hardware.rightMasterFalcon);
		hardware.rightSlaveFalcon.setInverted(InvertType.FollowMaster);
		resetDriveSensors(new Pose2d());
	}

	private void configureIndexerHardware() {
		var hardware = HardwareAdapter.IndexerHardware.getInstance();
		// Sparks
		hardware.masterSpark.restoreFactoryDefaults();
		hardware.slaveSpark.restoreFactoryDefaults();
		hardware.masterSpark.enableVoltageCompensation(kVoltageCompensation);
		hardware.slaveSpark.enableVoltageCompensation(kVoltageCompensation);
		hardware.masterSpark.setOpenLoopRampRate(0.1);
		hardware.slaveSpark.setOpenLoopRampRate(0.1);
		hardware.masterSpark.setInverted(true);
		hardware.slaveSpark.setInverted(true);
		double maxOutput = 0.8;
		hardware.masterSpark.getController().setOutputRange(-maxOutput, maxOutput);
		hardware.masterSpark.setSmartCurrentLimit((int) Math.round(30.0 / maxOutput));
		hardware.masterSpark.setSecondaryCurrentLimit(40.0 / maxOutput);
		// Talons
		hardware.leftVTalon.configFactoryDefault(kTimeoutMs);
		hardware.leftVTalon.enableVoltageCompensation(true);
		hardware.leftVTalon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		hardware.leftVTalon.configOpenloopRamp(0.1, kTimeoutMs);
		hardware.leftVTalon.setInverted(true);

		hardware.rightVTalon.configFactoryDefault(kTimeoutMs);
		hardware.rightVTalon.enableVoltageCompensation(true);
		hardware.rightVTalon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		hardware.rightVTalon.configOpenloopRamp(0.1, kTimeoutMs);
		hardware.rightVTalon.setInverted(true);

	}

	private void configureIntakeHardware() {
		var talon = HardwareAdapter.IntakeHardware.getInstance().talon;
		talon.configFactoryDefault(kTimeoutMs);
		talon.enableVoltageCompensation(true);
		talon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
		talon.configOpenloopRamp(0.1, kTimeoutMs);
		talon.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 30.0, 5.0, 2.0));
		talon.setInverted(true);
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
		hardware.masterSpark.setInverted(false);
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

	/**
	 * Updates the hardware to run with output values of {@link SubsystemBase}'s.
	 */
	void updateHardware(Set<SubsystemBase> enabledSubsystems) {
		mRumbleOutput = false;
		if (!mRobotConfig.disableHardwareUpdates) {
			if (enabledSubsystems.contains(mClimber)) updateClimber();
			if (enabledSubsystems.contains(mDrive)) updateDrivetrain();
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
		hardware.verticalSpark.setOutput(mClimber.getVerticalOutput());
		hardware.horizontalSpark.setOutput(mClimber.getAdjustingOutput());
		hardware.solenoid.setExtended(mClimber.getSolenoidOutput());
	}

	private void updateDrivetrain() {
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		hardware.falcons.forEach(this::handleReset);
		hardware.leftMasterFalcon.setOutput(mDrive.getDriveSignal().leftOutput);
		hardware.rightMasterFalcon.setOutput(mDrive.getDriveSignal().rightOutput);
	}

	private void updateIndexer() {
		var hardware = HardwareAdapter.IndexerHardware.getInstance();
		handleReset(hardware.leftVTalon);
		handleReset(hardware.rightVTalon);
		hardware.masterSpark.setOutput(mIndexer.getMasterSparkOutput());
		hardware.slaveSpark.setOutput(mIndexer.getSlaveSparkOutput());
		hardware.hopperSolenoid.setExtended(mIndexer.getHopperOutput());
		hardware.blockingSolenoid.setExtended(mIndexer.getBlockOutput());
		hardware.leftVTalon.setOutput(mIndexer.getLeftVTalonOutput());
		hardware.rightVTalon.setOutput(mIndexer.getRightVTalonOutput());
//		LiveGraph.add("indexerMasterAppliedOutput", hardware.masterSpark.getAppliedOutput());
//		LiveGraph.add("indexerMasterVelocity", hardware.masterEncoder.getVelocity());
//		LiveGraph.add("indexerSlaveAppliedOutput", hardware.slaveSpark.getAppliedOutput());
//		LiveGraph.add("indexerSlaveVelocity", hardware.slaveEncoder.getVelocity());
//		LiveGraph.add("indexerTargetVelocity", mIndexer.getMasterSparkOutput().getReference());
//		PowerDistributionPanel pdp = HardwareAdapter.MiscellaneousHardware.getInstance().pdp;
//		LiveGraph.add("indexerCurrent10", pdp.getCurrent(10));
//		LiveGraph.add("indexerCurrent11", pdp.getCurrent(11));
//		LiveGraph.add("intakeCurrent8", pdp.getCurrent(8));
//		LiveGraph.add("totalCurrent", pdp.getTotalCurrent());
//		LiveGraph.add("batteryVoltage", RobotController.getBatteryVoltage());
	}

	private void updateIntake() {
		var hardware = HardwareAdapter.IntakeHardware.getInstance();
		handleReset(hardware.talon);
		hardware.talon.setOutput(mIntake.getOutput());
		hardware.solenoid.setExtended(mIntake.getExtendedOutput());
	}

	public void updateLighting() {
		var hardware = HardwareAdapter.LightingHardware.getInstance();
		hardware.ledStrip.setData(mLighting.getOutput());
	}

	private void updateShooter() {
		var hardware = HardwareAdapter.ShooterHardware.getInstance();
		handleReset(hardware.masterSpark, hardware.slaveSpark);
		hardware.masterSpark.setOutput(mShooter.getFlywheelOutput());
		hardware.blockingSolenoid.setExtended(mShooter.getBlockingOutput());
		hardware.hoodSolenoid.setExtended(mShooter.getHoodOutput());
		mRumbleOutput |= mShooter.getRumbleOutput();
	}

	private void updateSpinner() {
		var hardware = HardwareAdapter.SpinnerHardware.getInstance();
		handleReset(hardware.talon);
		hardware.talon.setOutput(mSpinner.getOutput());
	}

	private void handleReset(Spark master, Spark slave) {
		if (mRobotConfig.checkFaults && slave.getStickyFault(FaultID.kHasReset)) {
			slave.follow(master);
			slave.clearFaults();
			Log.error(kLoggerTag, String.format("%s spark reset", slave.getName()));
		}
	}

	private void handleReset(Talon talon) {
		if (mRobotConfig.checkFaults && talon.hasResetOccurred()) {
			Log.error(kLoggerTag, String.format("%s talon reset", talon.getName()));
		}
	}

	private void handleReset(Falcon falcon) {
		if (mRobotConfig.checkFaults && falcon.hasResetOccurred()) {
			Log.error(kLoggerTag, String.format("%s falcon reset", falcon.getName()));
		}
	}
}
