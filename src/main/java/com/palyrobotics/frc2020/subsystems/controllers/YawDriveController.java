package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class YawDriveController extends Drive.DriveController {

	private ProfiledPIDController mController = new ProfiledPIDController(0.0, 0.0, 0.0,
			new TrapezoidProfile.Constraints());
	private Double mTargetYaw;
	private Double mTargetRealYaw;

	public YawDriveController() {
	}

	/**
	 * Signals should change only based on {@link Commands}. However, our {@link #mController} has
	 * internal states. We have to do our best to manage these solely based on commands. So, when our
	 * goal changes, we have to notify our controller. This ensures we can still remain in this
	 * controller and have different targets.
	 */
	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		//TODO: start with state.driveYawDegrees and increase until difference between target and state.driveYawDegrees = 0
		//Or don't use getDifference in angle, and instead just keep the state.driveYawDegrees the same.
		double wantedYawDegrees = commands.getDriveWantedYawDegrees(),
				realCurrentYawDegrees = state.driveYawDegrees,
				currentYawDegrees = Util.boundAngleNeg180to180Degrees(state.driveYawDegrees);
		LiveGraph.add("realYaw", realCurrentYawDegrees);
		CSVWriter.addData("realYaw", realCurrentYawDegrees);
		if (mTargetYaw == null || !Util.approximatelyEqual(mTargetYaw, wantedYawDegrees)) {
			mController.reset(realCurrentYawDegrees);
			mTargetYaw = wantedYawDegrees;
			mTargetRealYaw = realCurrentYawDegrees - Util.getDifferenceInAngleDegreesNeg180To180(currentYawDegrees, mTargetYaw);
		}
		LiveGraph.add("mTargetRealYaw", mTargetRealYaw);
		CSVWriter.addData("mTargetRealYaw", mTargetRealYaw);
		mController.setPID(mConfig.turnGains.p, mConfig.turnGains.i, mConfig.turnGains.d);
		mController.setConstraints(
				new TrapezoidProfile.Constraints(mConfig.turnGains.velocity, mConfig.turnGains.acceleration));
		var feedForwardCalculator = new SimpleMotorFeedforward(0.0, mConfig.turnGains.f, 0.0);
		double percentOutput = mController.calculate(realCurrentYawDegrees, mTargetRealYaw);
		LiveGraph.add("error", mTargetRealYaw - realCurrentYawDegrees);
		CSVWriter.addData("error", mTargetRealYaw - realCurrentYawDegrees);
		percentOutput += feedForwardCalculator.calculate(mController.getSetpoint().velocity);
		mOutputs.leftOutput.setPercentOutput(-percentOutput);
		mOutputs.rightOutput.setPercentOutput(percentOutput);
	}

}
