package com.palyrobotics.frc2020.subsystems.controllers;

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
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final PIDController mPidController = new PIDController(0.0, 0.0, 0.0);

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (mLimelight.isTargetFound()) {
			mPidController.setPID(mVisionConfig.gains.p, mVisionConfig.gains.i, mVisionConfig.gains.d);
			mPidController.setIntegratorRange(-mVisionConfig.gains.iMax, mVisionConfig.gains.iMax);
			double percentOutput = mPidController.calculate(mLimelight.getYawToTarget());
			percentOutput = Util.clamp(percentOutput, -kMaxAngularPower, kMaxAngularPower);
			percentOutput += Math.signum(percentOutput) * mVisionConfig.gainsS;
			double rightPercentOutput = percentOutput, leftPercentOutput = -percentOutput;
			mOutputs.leftOutput.setPercentOutput(leftPercentOutput);
			mOutputs.rightOutput.setPercentOutput(rightPercentOutput);
		} else {
			super.updateSignal(commands, state);
		}
	}
}
