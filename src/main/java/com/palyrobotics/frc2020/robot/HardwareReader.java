package com.palyrobotics.frc2020.robot;

import java.util.Set;

import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.robot.HardwareAdapter.*;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.DriverStation;

public class HardwareReader {

	public static final int kYawIndex = 0;
	/**
	 * A REV Color Match object is used to register and detect known colors. This can be calibrated
	 * ahead of time or during operation. This object uses euclidean distance to estimate the closest
	 * match with a given confidence range.
	 */
	public final ColorMatch mColorMatcher = new ColorMatch();
	private final double[] mGyroAngles = new double[3];

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
	void updateState(Set<SubsystemBase> enabledSubsystems, RobotState robotState) {
		readGameAndFieldState(robotState);
		if (enabledSubsystems.contains(Climber.getInstance())) readClimberState(robotState);
		if (enabledSubsystems.contains(Drive.getInstance())) readDriveState(robotState);
		if (enabledSubsystems.contains(Indexer.getInstance())) readIndexerState(robotState);
		if (enabledSubsystems.contains(Intake.getInstance())) readIntakeState(robotState);
		if (enabledSubsystems.contains(Shooter.getInstance())) readShooterState(robotState);
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
		var hardware = ClimberHardware.getInstance();
		robotState.climberPosition = hardware.verticalSparkEncoder.getPosition();
		robotState.climberVelocity = hardware.verticalSparkEncoder.getVelocity();
	}

	private void readDriveState(RobotState robotState) {
		var hardware = DrivetrainHardware.getInstance();
		hardware.gyro.getYawPitchRoll(mGyroAngles);
		robotState.driveYawDegrees = mGyroAngles[kYawIndex];
		robotState.driveLeftVelocity = hardware.leftMasterFalcon.getConvertedVelocity();
		robotState.driveRightVelocity = hardware.rightMasterFalcon.getConvertedVelocity();
		robotState.driveLeftPosition = hardware.leftMasterFalcon.getConvertedPosition();
		robotState.driveRightPosition = hardware.rightMasterFalcon.getConvertedPosition();
		LiveGraph.add("leftPosition", robotState.driveLeftPosition);
		LiveGraph.add("rightPosition", robotState.driveRightPosition);
		robotState.updateOdometry(robotState.driveYawDegrees, robotState.driveLeftPosition, robotState.driveRightPosition);
		LiveGraph.add("driveLeftPosition", robotState.driveLeftPosition);
		LiveGraph.add("driveLeftVelocity", robotState.driveLeftVelocity);
		LiveGraph.add("driveRightPosition", robotState.driveRightPosition);
		LiveGraph.add("driveRightVelocity", robotState.driveRightVelocity);
		LiveGraph.add("driveYaw", robotState.driveYawDegrees);
		LiveGraph.add("driveRightPercentOutput", hardware.rightMasterFalcon.getMotorOutputPercent());
		LiveGraph.add("driveLeftPercentOutput", hardware.leftMasterFalcon.getMotorOutputPercent());
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
		var hardware = IndexerHardware.getInstance();

		robotState.hasFrontLeftBall = !hardware.leftFrontInfrared.get();
		robotState.hasFrontRightBall = !hardware.rightFrontInfrared.get();
		robotState.hasMiddleBall = !hardware.leftMiddleInfrared.get();

		if (robotState.backInfraredReadings.getLinkedList().size() == 0) {
			robotState.backInfraredReadings.addValue(false);
			robotState.backInfraredReadings.addValue(false);
		}

		robotState.backInfraredReadings.addValue(robotState.hasMiddleBall);

		if (robotState.backInfraredReadings.getLinkedList().get(0).equals(true) && robotState.backInfraredReadings.getLinkedList().get(1).equals(false)) {
			robotState.guaranteedBallCount++;
		}
		robotState.possibleBallCount = 0;

		if (robotState.hasFrontRightBall) {
			robotState.possibleBallCount++;
		}
		if (robotState.hasFrontLeftBall) {
			robotState.possibleBallCount++;
		}
		if (robotState.hasMiddleBall) {
			robotState.possibleBallCount++;
		}

		robotState.totalBallCount = robotState.guaranteedBallCount + robotState.possibleBallCount;

		System.out.println(robotState.totalBallCount);

		robotState.indexerIsHopperExtended = hardware.hopperSolenoid.isExtended();
	}

	private void readIntakeState(RobotState robotState) {
		var hardware = IntakeHardware.getInstance();
		robotState.intakeIsExtended = hardware.solenoid.isExtended();
	}

	private void readShooterState(RobotState robotState) {
		var hardware = ShooterHardware.getInstance();
		LiveGraph.add("shooterFlywheelVelocity", hardware.masterEncoder.getVelocity());
		robotState.shooterFlywheelVelocity = hardware.masterEncoder.getVelocity();
		robotState.shooterIsHoodExtended = hardware.hoodSolenoid.isExtended();
		robotState.shooterIsBlockingExtended = hardware.blockingSolenoid.isExtended();
		robotState.shooterHoodIsInTransition = hardware.hoodSolenoid.isInTransition() || hardware.blockingSolenoid.isInTransition();
	}
}
