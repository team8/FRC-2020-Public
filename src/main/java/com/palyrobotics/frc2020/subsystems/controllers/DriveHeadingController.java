package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class DriveHeadingController extends Drive.DriveController {

	private ProfiledPIDController mController = new ProfiledPIDController(0.0, 0.0, 0.0,
			new TrapezoidProfile.Constraints());

	public DriveHeadingController() {
		mController.enableContinuousInput(0.0, 360.0);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double wantedHeadingDegrees = commands.getDriveWantedHeadingDegrees();
		if (!Util.approximatelyEqual(mController.getGoal().position, wantedHeadingDegrees)) {
			mController.reset(state.driveHeadingDegrees);
		}
		mController.setPID(mDriveConfig.turnGains.p, mDriveConfig.turnGains.i, mDriveConfig.turnGains.d);
		mController.setConstraints(
				new TrapezoidProfile.Constraints(mDriveConfig.turnGains.velocity, mDriveConfig.turnGains.acceleration));
		var feedForwardCalculator = new SimpleMotorFeedforward(0.0, mDriveConfig.turnGains.f, 0.0);
		double drivePercentOutput = mController.calculate(state.driveHeadingDegrees, wantedHeadingDegrees);
		drivePercentOutput += feedForwardCalculator.calculate(mController.getSetpoint().velocity);
		mDriveOutputs.leftOutput.setPercentOutput(-drivePercentOutput);
		mDriveOutputs.rightOutput.setPercentOutput(drivePercentOutput);
	}
}
