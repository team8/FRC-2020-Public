package com.palyrobotics.frc2019.util.archive;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.util.DriveSignal;

public class ProportionalDriveHelper {
	private DriveSignal mSignal = DriveSignal.getNeutralSignal();

	public DriveSignal pDrive(Commands commands) {
		double throttle = -Robot.getRobotState().leftStickInput.getY();
		double wheel = Robot.getRobotState().rightStickInput.getX();

		double rightPwm = throttle - wheel;
		double leftPwm = throttle + wheel;

		mSignal.leftMotor.setPercentOutput(leftPwm);
		mSignal.rightMotor.setPercentOutput(rightPwm);
		return mSignal;
	}

}