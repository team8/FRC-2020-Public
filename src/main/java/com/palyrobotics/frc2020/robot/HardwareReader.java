package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.sensors.PigeonIMU.PigeonState;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.robot.HardwareAdapter.*;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.DriverStation;

public class HardwareReader {

	private static final String kLoggerTag = Util.classToJsonName(HardwareReader.class);
	private static final int kYawIndex = 0, kYawAngularVelocityIndex = 2;
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	/**
	 * A REV Color Match object is used to register and detect known colors. This can be calibrated
	 * ahead of time or during operation. This object uses euclidean distance to estimate the closest
	 * match with a given confidence range.
	 */
	private final ColorMatch mColorMatcher = new ColorMatch();
	private final double[] mGyroAngles = new double[3], mGyroAngularVelocities = new double[3];

	public HardwareReader() {
		mColorMatcher.addColorMatch(SpinnerConstants.kCyanCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kGreenCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kRedCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kYellowCPTarget);
	}

	/**
	 * Takes all of the sensor data from the hardware, and unwraps it into the current
	 * {@link RobotState}.
	 */
	void updateState(Set<SubsystemBase> enabledSubsystems, RobotState state) {
		readGameAndFieldState(state);
		Robot.mDebugger.addPoint("readGameAndFieldState");
		if (enabledSubsystems.contains(Drive.getInstance())) readDriveState(state);
		Robot.mDebugger.addPoint("Drive");
		if (enabledSubsystems.contains(Indexer.getInstance())) readIndexerState(state);
		Robot.mDebugger.addPoint("Indexer");
		if (enabledSubsystems.contains(Intake.getInstance())) readIntakeState(state);
		Robot.mDebugger.addPoint("Intake");
		if (enabledSubsystems.contains(Shooter.getInstance())) readShooterState(state);
		Robot.mDebugger.addPoint("Shooter");
		if (enabledSubsystems.contains(Spinner.getInstance())) readSpinnerState(state);
		Robot.mDebugger.addPoint("Spinner");
	}

	private void readGameAndFieldState(RobotState state) {
		state.gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	private void readDriveState(RobotState state) {
		var hardware = DriveHardware.getInstance();
		/* Gyro */
		state.driveIsGyroReady = hardware.gyro.getState() == PigeonState.Ready;
		if (state.driveIsGyroReady) {
			hardware.gyro.getYawPitchRoll(mGyroAngles);
			state.driveYawDegrees = mGyroAngles[kYawIndex];
			hardware.gyro.getRawGyro(mGyroAngularVelocities);
			state.driveYawAngularVelocityDegrees = mGyroAngularVelocities[kYawAngularVelocityIndex];
		}
		/* Falcons */
		state.driveLeftVelocity = hardware.leftMasterFalcon.getConvertedVelocity();
		state.driveRightVelocity = hardware.rightMasterFalcon.getConvertedVelocity();
		state.driveLeftPosition = hardware.leftMasterFalcon.getConvertedPosition();
		state.driveRightPosition = hardware.rightMasterFalcon.getConvertedPosition();
//		LiveGraph.add("x", state.drivePoseMeters.getTranslation().getX());
//		LiveGraph.add("y", state.drivePoseMeters.getTranslation().getY());
//		LiveGraph.add("leftPosition", state.driveLeftPosition);
//		LiveGraph.add("rightPosition", state.driveRightPosition);
		/* Odometry */
		state.updateOdometry(state.driveYawDegrees, state.driveLeftPosition, state.driveRightPosition);
//		LiveGraph.add("driveLeftPosition", state.driveLeftPosition);

		LiveGraph.add("driveLeftVelocity", state.driveLeftVelocity);
//		LiveGraph.add("driveRightPosition", state.driveRightPosition);
		LiveGraph.add("driveRightVelocity", state.driveRightVelocity);
//		LiveGraph.add("driveYaw", state.driveYawDegrees);
//		LiveGraph.add("driveRightPercentOutput", hardware.rightMasterFalcon.getMotorOutputPercent());
//		LiveGraph.add("driveLeftPercentOutput", hardware.leftMasterFalcon.getMotorOutputPercent());
		hardware.falcons.forEach(this::checkFalconFaults);
	}

	private void readIndexerState(RobotState state) {
		var hardware = IndexerHardware.getInstance();
		state.indexerHasBackBall = !hardware.backInfrared.get();
		state.indexerHasFrontBall = !hardware.frontInfrared.get();
		state.indexerHasTopBall = !hardware.topInfrared.get();
		state.indexerIsHopperExtended = hardware.hopperSolenoid.isExtended();
		state.indexerMasterVelocity = hardware.masterEncoder.getVelocity();
		checkSparkFaults(hardware.masterSpark);
		checkSparkFaults(hardware.slaveSpark);
		checkTalonFaults(hardware.leftVTalon);
	}

	private void readIntakeState(RobotState state) {
		var hardware = IntakeHardware.getInstance();
		state.intakeIsExtended = hardware.solenoid.isExtended();
		checkTalonFaults(hardware.talon);
	}

	private void readShooterState(RobotState state) {
		var hardware = ShooterHardware.getInstance();
//		LiveGraph.add("shooterFlywheelVelocity", hardware.masterEncoder.getVelocity());
//		LiveGraph.add("shooterAppliedOutput", hardware.masterSpark.getAppliedOutput());
		state.shooterFlywheelVelocity = hardware.masterEncoder.getVelocity();
		state.shooterIsHoodExtended = hardware.hoodSolenoid.isExtended();
		state.shooterIsBlockingExtended = hardware.blockingSolenoid.isExtended();
		state.shooterHoodIsInTransition = hardware.hoodSolenoid.isInTransition() || hardware.blockingSolenoid.isInTransition();
		checkSparkFaults(hardware.masterSpark);
		checkSparkFaults(hardware.slaveSpark);
	}

	private void readSpinnerState(RobotState state) {
		state.detectedRGBValues = SpinnerHardware.getInstance().colorSensor.getColor();
		state.closestColorRGB = mColorMatcher.matchClosestColor(state.detectedRGBValues);
		if (state.closestColorRGB.color == SpinnerConstants.kCyanCPTarget) {
			state.closestColorString = "C";
		} else if (state.closestColorRGB.color == SpinnerConstants.kYellowCPTarget) {
			state.closestColorString = "Y";
		} else if (state.closestColorRGB.color == SpinnerConstants.kGreenCPTarget) {
			state.closestColorString = "G";
		} else if (state.closestColorRGB.color == SpinnerConstants.kRedCPTarget) {
			state.closestColorString = "R";
		}
		state.closestColorConfidence = state.closestColorRGB.confidence;
//		System.out.println(Spinner.getInstance().directionToGoalColor(state.closestColorString, state.gameData));
	}

	private void checkSparkFaults(Spark spark) {
		if (mRobotConfig.checkFaults) {
			boolean wasAnyFault = false;
			for (var value : FaultID.values()) {
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
