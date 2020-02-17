package com.palyrobotics.frc2020.subsystems.controllers.drive;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class AlignDriveController extends ChezyDriveController {

	private static final String kLoggerTag = Util.classToJsonName(AlignDriveController.class);
	private final Limelight mLimelight = Limelight.getInstance();
	private final PIDController mPidController = new PIDController(0.0, 0.0, 0.0);
	private final ProfiledPIDController mProfiledController = new ProfiledPIDController(0.0, 0.0, 0.0, new TrapezoidProfile.Constraints());
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private boolean mNeedsProfiledReset = true;

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (mLimelight.isTargetFound()) {
			double yawToTargetDegrees = mLimelight.getYawToTarget();
			double absoluteYawErrorDegrees = Math.abs(yawToTargetDegrees);
			if (absoluteYawErrorDegrees > mVisionConfig.acceptableYawError) {
				double percentOutput;
				if (absoluteYawErrorDegrees > 1.0) {
					if (mNeedsProfiledReset) {
						Log.debug(kLoggerTag, "Reset profiled PID controller");
						mProfiledController.reset(yawToTargetDegrees, state.driveYawVelocity);
						mNeedsProfiledReset = false;
					}
					var profiledGains = mVisionConfig.profiledGains;
					mProfiledController.setPID(profiledGains.p, profiledGains.i, profiledGains.d);
					mProfiledController.setIntegratorRange(-profiledGains.iMax, profiledGains.iMax);
					mProfiledController.setConstraints(new TrapezoidProfile.Constraints(mConfig.turnGains.velocity, mConfig.turnGains.acceleration));
					double targetVelocity = mProfiledController.getSetpoint().velocity;
					percentOutput = mProfiledController.calculate(yawToTargetDegrees) + Math.signum(targetVelocity) * mConfig.turnGainsS + targetVelocity * mConfig.turnGains.f;
				} else {
					var preciseGains = mVisionConfig.preciseGains;
					mPidController.setPID(preciseGains.p, preciseGains.i, preciseGains.d);
					mPidController.setIntegratorRange(-preciseGains.iMax, preciseGains.iMax);
					percentOutput = mPidController.calculate(yawToTargetDegrees) + Math.signum(-yawToTargetDegrees) * mConfig.turnGainsS;
					mNeedsProfiledReset = true;
				}
				mOutputs.leftOutput.setPercentOutput(-percentOutput);
				mOutputs.rightOutput.setPercentOutput(percentOutput);
			} else {
				mOutputs.leftOutput.setIdle();
				mOutputs.rightOutput.setIdle();
				mNeedsProfiledReset = true;
			}
		} else {
			super.updateSignal(commands, state);
			mNeedsProfiledReset = true;
		}
	}
}
