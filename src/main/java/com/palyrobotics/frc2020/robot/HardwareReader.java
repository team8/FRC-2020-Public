package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.robot.HardwareAdapter.*;
import com.palyrobotics.frc2020.subsystems.*;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.DriverStation;

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
		if (enabledSubsystems.contains(Climber.getInstance()))
			readClimberState(robotState);
		if (enabledSubsystems.contains(Drive.getInstance()))
			readDriveState(robotState);
		if (enabledSubsystems.contains(Indexer.getInstance()))
			readIndexerState(robotState);
		if (enabledSubsystems.contains(Intake.getInstance()))
			readIntakeState(robotState);
		if (enabledSubsystems.contains(Shooter.getInstance()))
			readShooterState(robotState);
	}

	private void readGameAndFieldState(RobotState robotState) {
		robotState.gameData = DriverStation.getInstance().getGameSpecificMessage();
		robotState.detectedRGBValues = SpinnerHardware.getInstance().colorSensor.getColor();
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
		var climber = ClimberHardware.getInstance();
		robotState.climberPosition = climber.verticalSparkEncoder.getPosition();
		robotState.climberVelocity = climber.verticalSparkEncoder.getVelocity();
	}

	private void readDriveState(RobotState robotState) {
		var drivetrain = DrivetrainHardware.getInstance();
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
		var indexerHardware = IndexerHardware.getInstance();
		robotState.hasBackBall = indexerHardware.backInfrared.get();
		robotState.hasFrontBall = indexerHardware.frontInfrared.get();
		robotState.hasTopBall = indexerHardware.topInfrared.get();
	}

	private void readIntakeState(RobotState robotState) {
		var hardware = IntakeHardware.getInstance();
		robotState.intakeUpDownSolenoidState.updateExtended(hardware.upDownSolenoid.get());
	}

	private void readShooterState(RobotState robotState) {
		var shooterHardware = ShooterHardware.getInstance();
		robotState.shooterVelocity = shooterHardware.masterEncoder.getVelocity();
		robotState.shooterHoodSolenoidState.updateExtended(shooterHardware.hoodSolenoid.get());
		robotState.shooterBlockingSolenoidState.updateExtended(shooterHardware.blockingSolenoid.get());
	}
}
