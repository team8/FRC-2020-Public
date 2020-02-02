package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.controller.PIDController;

public class VisionYawAlignDriveController extends ChezyDriveController {

	private static final double kMaxAngularPower = 0.4;
	private final Limelight mLimelight = Limelight.getInstance();
	private VisionConfig mConfig = Configs.get(VisionConfig.class);
	private int mPipelineBeingUsed = 0;
	private final PIDController mPidController = new PIDController(0.0, 0.0, 0.0);

	public VisionYawAlignDriveController() {
		mLimelight.setPipeline(0);
		mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
		mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		boolean targetFound = mLimelight.isTargetFound();
		double angleToTarget = mLimelight.getYawToTarget();

		if (targetFound) {
			mPidController.setPID(mConfig.gains.p, mConfig.gains.i, mConfig.gains.d);
			double angularPower = mPidController.calculate(angleToTarget);
			Util.clamp(angularPower, -kMaxAngularPower, kMaxAngularPower);
			// if (Math.abs(angularPower) > kMaxAngularPower)
			// angularPower = angularPower > 0 ? kMaxAngularPower : -kMaxAngularPower;
			double rightOutput = angularPower, leftOutput = -angularPower;
			mDriveOutputs.leftOutput.setPercentOutput(leftOutput);
			mDriveOutputs.rightOutput.setPercentOutput(rightOutput);
		} else {
			/*
			* alternates every cycle target not found (pipeline 0 for normal, pipeline 1
			* for 2x hardware zoom Ensures distance is not reason target is not being
			* seen...
			*/
			// mPipelineBeingUsed = mPipelineBeingUsed == 1 ? 0 : 1;
			// mLimelight.setPipeline(mPipelineBeingUsed);
			super.updateSignal(commands, robotState);
		}
	}
}
