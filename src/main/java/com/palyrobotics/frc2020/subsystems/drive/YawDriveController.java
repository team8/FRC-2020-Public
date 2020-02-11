package com.palyrobotics.frc2020.subsystems.drive;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class YawDriveController extends Drive.DriveController {

	private ProfiledPIDController mController = new ProfiledPIDController(0.0, 0.0, 0.0,
			new TrapezoidProfile.Constraints());
	private Double mTargetYaw;

	public YawDriveController() {
		mController.enableContinuousInput(-180.0, 180.0);
	}

	/**
	 * Signals should change only based on {@link Commands}. However, our {@link #mController} has
	 * internal states. We have to do our best to manage these solely based on commands. So, when our
	 * goal changes, we have to notify our controller. This ensures we can still remain in this
	 * controller and have different targets.
	 */
	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double wantedYawDegrees = commands.getDriveWantedYawDegrees(),
				currentYawDegrees = Util.boundAngleNeg180to180Degrees(state.driveYawDegrees);
		if (mTargetYaw == null || !Util.approximatelyEqual(mTargetYaw, wantedYawDegrees)) {
			mController.reset(currentYawDegrees);
			mTargetYaw = wantedYawDegrees;
		}
		mController.setPID(mConfig.turnGains.p, mConfig.turnGains.i, mConfig.turnGains.d);
		mController.setConstraints(
				new TrapezoidProfile.Constraints(mConfig.turnGains.velocity, mConfig.turnGains.acceleration));
		var feedForwardCalculator = new SimpleMotorFeedforward(mConfig.turnGainsS, mConfig.turnGains.f, 0.0);
		double percentOutput = mController.calculate(currentYawDegrees, wantedYawDegrees);
		percentOutput += feedForwardCalculator.calculate(mController.getSetpoint().velocity);
		mOutputs.leftOutput.setPercentOutput(-percentOutput);
		mOutputs.rightOutput.setPercentOutput(percentOutput);
	}

}
