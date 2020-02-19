package com.palyrobotics.frc2020.subsystems.controllers.drive;

import static com.palyrobotics.frc2020.util.Util.*;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.Robot;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class YawDriveController extends Drive.DriveController {

	private static final String kLoggerTag = Util.classToJsonName(YawDriveController.class);
	private ProfiledPIDController mController = new ProfiledPIDController(
			0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(), Robot.kPeriod);
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
				currentYawDegrees = boundAngleNeg180to180Degrees(state.driveYawDegrees);
		if (mTargetYaw == null || getDifferenceInAngleDegrees(mTargetYaw, wantedYawDegrees) > kEpsilon) {
			Log.debug(kLoggerTag, "Reset profiled PID controller");
			mController.reset(currentYawDegrees, state.driveYawAngularVelocityDegrees);
			mTargetYaw = wantedYawDegrees;
		}
		var turnGains = mConfig.turnGains;
		mController.setPID(turnGains.p, turnGains.i, turnGains.d);
		mController.setIntegratorRange(-turnGains.iMax, turnGains.iMax);
		mController.setConstraints(new TrapezoidProfile.Constraints(turnGains.velocity, turnGains.acceleration));
		double percentOutput = mController.calculate(currentYawDegrees, wantedYawDegrees);
		TrapezoidProfile.State setpoint = mController.getSetpoint();
		double targetVelocity = setpoint.velocity;
		percentOutput += Math.signum(targetVelocity) * mConfig.turnGainsS + targetVelocity * turnGains.f;
		mOutputs.leftOutput.setPercentOutput(-percentOutput);
		mOutputs.rightOutput.setPercentOutput(percentOutput);
//		LiveGraph.add("currentYaw", state.driveYawDegrees);
//		LiveGraph.add("targetYaw", mController.getSetpoint().position);
	}

}
