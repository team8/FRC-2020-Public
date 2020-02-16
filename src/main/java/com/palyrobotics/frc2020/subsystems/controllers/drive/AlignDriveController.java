package com.palyrobotics.frc2020.subsystems.controllers.drive;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.controller.PIDController;

public class AlignDriveController extends ChezyDriveController {

	private static final double kMaxAngularPower = 0.4;
	private final Limelight mLimelight = Limelight.getInstance();
	private final PIDController mPidController = new PIDController(0.0, 0.0, 0.0);
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (mLimelight.isTargetFound()) {
			if (Math.abs(mLimelight.getYawToTarget()) > mVisionConfig.acceptableYawError) {
				if (mLimelight.getPipeline() == 0) {
					mPidController.setPID(mVisionConfig.oneTimesZoomGains.p, mVisionConfig.oneTimesZoomGains.i, mVisionConfig.oneTimesZoomGains.d);
					mPidController.setIntegratorRange(-mVisionConfig.oneTimesZoomGains.iMax, mVisionConfig.oneTimesZoomGains.iMax);
				} else if (mLimelight.getPipeline() == 1) {
					mPidController.setPID(mVisionConfig.twoTimesZoomGains.p, mVisionConfig.twoTimesZoomGains.i, mVisionConfig.twoTimesZoomGains.d);
					mPidController.setIntegratorRange(-mVisionConfig.twoTimesZoomGains.iMax, mVisionConfig.twoTimesZoomGains.iMax);
				}
				double yawToTarget = mLimelight.getYawToTarget();
				double percentOutput = mPidController.calculate(yawToTarget);
				percentOutput = Util.clamp(percentOutput, -kMaxAngularPower, kMaxAngularPower);
				double feedForward = Math.signum(-yawToTarget) * mConfig.turnGainsS;
				double rightPercentOutput = percentOutput + feedForward, leftPercentOutput = -percentOutput - feedForward;
				mOutputs.leftOutput.setPercentOutput(leftPercentOutput);
				mOutputs.rightOutput.setPercentOutput(rightPercentOutput);
			} else {
				mOutputs.leftOutput.setIdle();
				mOutputs.rightOutput.setIdle();
			}
		} else {
			super.updateSignal(commands, state);
		}
	}
}
