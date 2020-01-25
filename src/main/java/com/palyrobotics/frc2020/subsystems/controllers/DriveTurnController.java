package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class DriveTurnController extends Drive.DriveController {

	private ProfiledPIDController mController = new ProfiledPIDController(0.0, 0.0, 0.0,
			new TrapezoidProfile.Constraints());

	public DriveTurnController() {
		mController.enableContinuousInput(0.0, 360.0);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		mController.setPID(mDriveConfig.turnGains.p, mDriveConfig.turnGains.i, mDriveConfig.turnGains.d);
		mController.setConstraints(
				new TrapezoidProfile.Constraints(mDriveConfig.turnGains.velocity, mDriveConfig.turnGains.acceleration));
		var feedForwardCalculator = new SimpleMotorFeedforward(DriveConstants.kS, mDriveConfig.turnGains.f, 0.0);
		double drivePercentOutput = mController.calculate(state.driveHeadingDegrees,
				commands.getDriveWantedHeadingDegrees());
		drivePercentOutput += feedForwardCalculator.calculate(mController.getSetpoint().velocity);
		mDriveOutputs.leftOutput.setPercentOutput(-drivePercentOutput);
		mDriveOutputs.rightOutput.setPercentOutput(drivePercentOutput);
	}
}
