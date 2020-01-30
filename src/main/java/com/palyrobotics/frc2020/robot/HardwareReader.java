package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.config.Configs;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpiutil.CircularBuffer;

public class HardwareReader {

	/**
	 * A REV Color Match object is used to register and detect known colors. This
	 * can be calibrated ahead of time or during operation.
	 *
	 * This object uses euclidean distance to estimate the closest match with a
	 * given confidence range.
	 */
	public final ColorMatch mColorMatcher = new ColorMatch();
	private final double[] mGyroValues = new double[3];

	public HardwareReader() {
		mColorMatcher.addColorMatch(SpinnerConstants.kCyanCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kGreenCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kRedCPTarget);
		mColorMatcher.addColorMatch(SpinnerConstants.kYellowCPTarget);
	}

	/**
	 * Takes all of the sensor data from the hardware, and unwraps it into the
	 * current {@link RobotState}.
	 */
	void updateState(Set<SubsystemBase> enabledSubsystems, RobotState robotState) {
		readGameAndFieldState(robotState);
		if (enabledSubsystems.contains(Climber.getInstance())) {
			readClimberState(robotState);
		}
		if (enabledSubsystems.contains(Drive.getInstance())) {
			readDriveState(robotState);
		}
		if (enabledSubsystems.contains(Indexer.getInstance())) {
			readIndexerState(robotState);
		}
		if (enabledSubsystems.contains(Shooter.getInstance())) {
			readShooterState(robotState);
		}
	}

	private void readGameAndFieldState(RobotState robotState) {
		robotState.gameData = DriverStation.getInstance().getGameSpecificMessage();
		robotState.detectedRGBValues = HardwareAdapter.SpinnerHardware.getInstance().colorSensor.getColor();
		robotState.closestColorRGB = mColorMatcher.matchClosestColor(robotState.detectedRGBValues);
		if (robotState.closestColorRGB.color == SpinnerConstants.kCyanCPTarget) {
			robotState.closestColorString = "Cyan";
		} else if (robotState.closestColorRGB.color == SpinnerConstants.kYellowCPTarget) {
			robotState.closestColorString = "Yellow";
		} else if (robotState.closestColorRGB.color == SpinnerConstants.kGreenCPTarget) {
			robotState.closestColorString = "Green";
		} else if (robotState.closestColorRGB.color == SpinnerConstants.kRedCPTarget) {
			robotState.closestColorString = "Red";
		}
		robotState.closestColorConfidence = robotState.closestColorRGB.confidence;
	}

	private void readClimberState(RobotState robotState) {
		var climber = HardwareAdapter.ClimberHardware.getInstance();
		robotState.climberPosition = climber.verticalSpark.getEncoder().getPosition();
		robotState.climberMedianVelocity = robotState.climberVelocityFilter
				.calculate(climber.verticalSpark.getEncoder().getVelocity());
	}

	private void readDriveState(RobotState robotState) {
		var drivetrain = HardwareAdapter.DrivetrainHardware.getInstance();
		var gyroAngles = new double[3];
		drivetrain.gyro.getYawPitchRoll(gyroAngles);
		robotState.driveYawDegrees = gyroAngles[0];
		robotState.driveLeftVelocity = drivetrain.leftMasterEncoder.getVelocity() / 60.0;
		robotState.driveRightVelocity = drivetrain.rightMasterEncoder.getVelocity() / 60.0;
		robotState.driveLeftPosition = drivetrain.leftMasterEncoder.getPosition();
		robotState.driveRightPosition = drivetrain.rightMasterEncoder.getPosition();
		robotState.updateOdometry(robotState.driveYawDegrees, robotState.driveLeftPosition,
				robotState.driveRightPosition);
	}

	// private void readDriveState(RobotState robotState) {
	// var drivetrain = HardwareAdapter.DrivetrainHardware.getInstance();
	// drivetrain.gyro.getYawPitchRoll(mGyroValues);
	// robotState.driveYawDegrees = mGyroValues[0];
	// robotState.driveLeftVelocity =
	// drivetrain.leftMasterFalcon.getConvertedVelocity();
	// robotState.driveRightVelocity =
	// drivetrain.rightMasterFalcon.getConvertedVelocity();
	// robotState.driveLeftPosition =
	// drivetrain.leftMasterFalcon.getConvertedPosition();
	// robotState.driveRightPosition =
	// drivetrain.rightMasterFalcon.getConvertedPosition();
	// robotState.updateOdometry(robotState.driveYawDegrees,
	// robotState.driveLeftPosition,
	// robotState.driveRightPosition);
	// }

	private void readIndexerState(RobotState robotState) {
		var indexerConfig = Configs.get(IndexerConfig.class);
		var indexerHardware = HardwareAdapter.IndexerHardware.getInstance();
		Ultrasonic backUltrasonic = indexerHardware.backUltrasonic, frontUltrasonic = indexerHardware.frontUltrasonic,
				topUltrasonic = indexerHardware.topUltrasonic;
		robotState.backIndexerUltrasonicReadings.addFirst(backUltrasonic.getRangeInches());
		robotState.frontIndexerUltrasonicReadings.addFirst(frontUltrasonic.getRangeInches());
		robotState.topIndexerUltrasonicReadings.addFirst(topUltrasonic.getRangeInches());
		robotState.hasBackUltrasonicBall = hasBallFromReadings(robotState.backIndexerUltrasonicReadings,
				indexerConfig.ballInchTolerance, indexerConfig.ballCountRequired);
		robotState.hasFrontUltrasonicBall = hasBallFromReadings(robotState.frontIndexerUltrasonicReadings,
				indexerConfig.ballInchTolerance, indexerConfig.ballCountRequired);
		robotState.hasTopUltrasonicBall = hasBallFromReadings(robotState.topIndexerUltrasonicReadings,
				indexerConfig.ballInchTolerance, indexerConfig.ballCountRequired);
	}

	private void readShooterState(RobotState robotState) {
		var shooterHardware = HardwareAdapter.ShooterHardware.getInstance();
		robotState.shooterVelocity = shooterHardware.masterEncoder.getVelocity();
		robotState.shooterHoodSolenoidState.updateExtended(shooterHardware.hoodSolenoid.get());
		robotState.shooterBlockingSolenoidState.updateExtended(shooterHardware.blockingSolenoid.get());
	}

	private boolean hasBallFromReadings(CircularBuffer readings, double tolerance, int requiredCount) {
		int count = 0;
		for (int i = 0; i < RobotState.kUltrasonicBufferSize; i++) {
			if (readings.get(i) <= tolerance)
				count++;
		}
		return count >= requiredCount;
	}
}
