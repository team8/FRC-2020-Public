package com.palyrobotics.frc2020.robot;

import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.DriverStation;

public class HardwareReader {

	/**
	 * A REV Color Match object is used to register and detect known colors. This
	 * can be calibrated ahead of time or during operation.
	 *
	 * <p>
	 * This object uses euclidean distance to estimate the closest match with a
	 * given confidence range.
	 */
	public final ColorMatch mColorMatcher = new ColorMatch();

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
	void updateState(RobotState robotState) {
		var drivetrain = HardwareAdapter.DrivetrainHardware.getInstance();

		robotState.driveHeading = drivetrain.gyro.getFusedHeading();

		// TODO: Update with position/velocity conversions
		robotState.driveLeftVelocity = drivetrain.leftMasterFalcon.getSelectedSensorVelocity() / 60.0;
		robotState.driveRightVelocity = drivetrain.rightMasterFalcon.getSelectedSensorVelocity() / 60.0;
		robotState.driveLeftPosition = drivetrain.leftMasterFalcon.getSelectedSensorPosition();
		robotState.driveRightPosition = drivetrain.rightMasterFalcon.getSelectedSensorPosition();

		// Updating color sensor data
		robotState.detectedRGBVals = HardwareAdapter.SpinnerHardware.getInstance().colorSensor.getColor();
		robotState.closestColorRGB = mColorMatcher.matchClosestColor(robotState.detectedRGBVals);
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

		// For testing purposes
		// System.out.println(robotState.closestColorString + " with confidence level of
		// " + (robotState.closestColorConfidence * 100));
		// System.out.println(robotState.detectedRGBVals.red + ", " +
		// robotState.detectedRGBVals.green + ", " + robotState.detectedRGBVals.blue);

		robotState.gameData = DriverStation.getInstance().getGameSpecificMessage();
		// if (robotState.gameData.length() > 0) {
		// System.out.printf("Game data has been found, color is: %s%n",
		// robotState.gameData);
		// }

		robotState.updateOdometry(robotState.driveHeading, robotState.driveLeftPosition, robotState.driveRightPosition);
	}
}
