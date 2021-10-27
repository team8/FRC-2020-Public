package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Holds the current physical state of the robot from our sensors.
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	public static final String kLoggerTag = Util.classToJsonName(RobotState.class);

	private static final int kYawIndex = 0, kYawAngularVelocityIndex = 2;
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	private final ColorMatch mColorMatcher = new ColorMatch();
	private final double[] mGyroAngles = new double[3], mGyroAngularVelocities = new double[3];

	public RobotState() {
		mColorMatcher.addColorMatch(SpinnerConstants.kCyanCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kGreenCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kRedCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kYellowCPTarget);
	}

	void updateState(Set<SubsystemBase> enabledSubsystems) {
		readGameAndFieldState();
		Robot.mDebugger.addPoint("readGameAndFieldState");
		if (enabledSubsystems.contains(Drive.getInstance())) readDriveState();
		Robot.mDebugger.addPoint("Drive");
		if (enabledSubsystems.contains(Indexer.getInstance())) readIndexerState();
		Robot.mDebugger.addPoint("Indexer");
		if (enabledSubsystems.contains(Intake.getInstance())) readIntakeState();
		Robot.mDebugger.addPoint("Intake");
		if (enabledSubsystems.contains(Shooter.getInstance())) readShooterState();
		Robot.mDebugger.addPoint("Shooter");
		if (enabledSubsystems.contains(Spinner.getInstance())) readSpinnerState();
		Robot.mDebugger.addPoint("Spinner");
	}

	/* Drive */
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public double driveYawDegrees, driveYawAngularVelocityDegrees;
	public boolean driveIsQuickTurning, driveIsSlowTurning;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePoseMeters = new Pose2d();
	public double driveVelocityMetersPerSecond;
	public boolean driveIsGyroReady;

	/* Indexer */
	public boolean indexerIsHopperExtended;
	public boolean indexerHasBackBall, indexerHasFrontBall, indexerHasTopBall;
	public double indexerMasterVelocity;

	/* Intake */
	public boolean intakeIsExtended;

	/* Shooter */
	public double shooterFlywheelVelocity;
	public boolean shooterIsReadyToShoot;
	public boolean shooterIsHoodExtended, shooterIsBlockingExtended;
	public boolean shooterHoodIsInTransition;

	/* Spinner */
	public String closestColorString;
	public double closestColorConfidence;
	public Color detectedRGBValues;
	public ColorMatchResult closestColorRGB;

	/* Game and Field */
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public String gameData;

	public void resetOdometry(Pose2d pose) {
		driveOdometry.resetPosition(pose, pose.getRotation());
		drivePoseMeters = driveOdometry.getPoseMeters();
		driveVelocityMetersPerSecond = 0.0;
		Log.info(kLoggerTag, String.format("Odometry reset to: %s", pose));
	}

	public void updateOdometry(double yawDegrees, double leftMeters, double rightMeters) {
		drivePoseMeters = driveOdometry.update(Rotation2d.fromDegrees(yawDegrees), leftMeters, rightMeters);
		ChassisSpeeds speeds = DriveConstants.kKinematics.toChassisSpeeds(new DifferentialDriveWheelSpeeds(driveLeftVelocity, driveRightVelocity));
		driveVelocityMetersPerSecond = Math.sqrt(Math.pow(speeds.vxMetersPerSecond, 2) + Math.pow(speeds.vyMetersPerSecond, 2));
	}

	private void readGameAndFieldState() {
		this.gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	private void readDriveState() {
		var hardware = Drive.getInstance();
		/* Gyro */
		this.driveIsGyroReady = hardware.gyro.getState() == PigeonIMU.PigeonState.Ready;
		if (this.driveIsGyroReady) {
			hardware.gyro.getYawPitchRoll(mGyroAngles);
			this.driveYawDegrees = mGyroAngles[kYawIndex];
			hardware.gyro.getRawGyro(mGyroAngularVelocities);
			this.driveYawAngularVelocityDegrees = mGyroAngularVelocities[kYawAngularVelocityIndex];
		}
		/* Falcons */
		this.driveLeftVelocity = hardware.leftMasterFalcon.getConvertedVelocity();
		this.driveRightVelocity = hardware.rightMasterFalcon.getConvertedVelocity();
		this.driveLeftPosition = hardware.leftMasterFalcon.getConvertedPosition();
		this.driveRightPosition = hardware.rightMasterFalcon.getConvertedPosition();
//		LiveGraph.add("x", state.drivePoseMeters.getTranslation().getX());
//		LiveGraph.add("y", state.drivePoseMeters.getTranslation().getY());
//		LiveGraph.add("leftPosition", state.driveLeftPosition);
//		LiveGraph.add("rightPosition", state.driveRightPosition);
		/* Odometry */
		this.updateOdometry(this.driveYawDegrees, this.driveLeftPosition, this.driveRightPosition);
//		LiveGraph.add("driveLeftPosition", state.driveLeftPosition);

		LiveGraph.add("driveLeftVelocity", this.driveLeftVelocity);
//		LiveGraph.add("driveRightPosition", state.driveRightPosition);
		LiveGraph.add("driveRightVelocity", this.driveRightVelocity);
//		LiveGraph.add("driveYaw", state.driveYawDegrees);
//		LiveGraph.add("driveRightPercentOutput", hardware.rightMasterFalcon.getMotorOutputPercent());
//		LiveGraph.add("driveLeftPercentOutput", hardware.leftMasterFalcon.getMotorOutputPercent());
		hardware.falcons.forEach(this::checkFalconFaults);
	}

	private void readIndexerState() {
		var hardware = Indexer.getInstance();
		this.indexerHasBackBall = !hardware.backInfrared.get();
		this.indexerHasFrontBall = !hardware.frontInfrared.get();
		this.indexerHasTopBall = !hardware.topInfrared.get();
		this.indexerIsHopperExtended = hardware.hopperSolenoid.isExtended();
		this.indexerMasterVelocity = hardware.masterEncoder.getVelocity();
		checkSparkFaults(hardware.masterSpark);
		checkSparkFaults(hardware.slaveSpark);
		checkTalonFaults(hardware.leftVTalon);
	}

	private void readIntakeState() {
		var hardware = Intake.getInstance();
		this.intakeIsExtended = hardware.solenoid.isExtended();
		checkTalonFaults(hardware.talon);
	}

	private void readShooterState() {
		var hardware = Shooter.getInstance();
//		LiveGraph.add("shooterFlywheelVelocity", hardware.masterEncoder.getVelocity());
//		LiveGraph.add("shooterAppliedOutput", hardware.masterSpark.getAppliedOutput());
		this.shooterFlywheelVelocity = hardware.masterEncoder.getVelocity();
		this.shooterIsHoodExtended = hardware.hoodSolenoid.isExtended();
		this.shooterIsBlockingExtended = hardware.blockingSolenoid.isExtended();
		this.shooterHoodIsInTransition = hardware.hoodSolenoid.isInTransition() || hardware.blockingSolenoid.isInTransition();
		checkSparkFaults(hardware.masterSpark);
		checkSparkFaults(hardware.slaveSpark);
	}

	private void readSpinnerState() {
		this.detectedRGBValues = Spinner.getInstance().colorSensor.getColor();
		this.closestColorRGB = mColorMatcher.matchClosestColor(this.detectedRGBValues);
		if (this.closestColorRGB.color == SpinnerConstants.kCyanCPTarget) {
			this.closestColorString = "C";
		} else if (this.closestColorRGB.color == SpinnerConstants.kYellowCPTarget) {
			this.closestColorString = "Y";
		} else if (this.closestColorRGB.color == SpinnerConstants.kGreenCPTarget) {
			this.closestColorString = "G";
		} else if (this.closestColorRGB.color == SpinnerConstants.kRedCPTarget) {
			this.closestColorString = "R";
		}
		this.closestColorConfidence = this.closestColorRGB.confidence;
//		System.out.println(Spinner.getInstance().directionToGoalColor(state.closestColorString, state.gameData));
	}

	private void checkSparkFaults(Spark spark) {
		if (mRobotConfig.checkFaults) {
			boolean wasAnyFault = false;
			for (var value : CANSparkMax.FaultID.values()) {
				boolean isFaulted = spark.getStickyFault(value);
				if (isFaulted) {
					Log.error(kLoggerTag, String.format("Spark %d fault: %s", spark.getDeviceId(), value));
					wasAnyFault = true;
				}
			}
			if (wasAnyFault) {
				spark.clearFaults();
			}
		}
	}

	private void checkTalonFaults(Talon talon) {
		if (mRobotConfig.checkFaults) {
			var faults = new StickyFaults();
			talon.getStickyFaults(faults);
			if (faults.hasAnyFault()) {
				Log.error(kLoggerTag, String.format("%s faults: %s", talon.getName(), faults));
				talon.clearStickyFaults();
			}
		}
	}

	private void checkFalconFaults(Falcon falcon) {
		if (mRobotConfig.checkFaults) {
			var faults = new StickyFaults();
			falcon.getStickyFaults(faults);
			if (faults.hasAnyFault()) {
				Log.error(kLoggerTag, String.format("%s faults: %s", falcon.getName(), faults));
				falcon.clearStickyFaults();
			}
		}
	}
}
