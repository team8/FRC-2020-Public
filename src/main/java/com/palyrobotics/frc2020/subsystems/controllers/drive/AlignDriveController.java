package com.palyrobotics.frc2020.subsystems.controllers.drive;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SynchronousPIDF;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.MedianFilter;

public class AlignDriveController extends ChezyDriveController {

	private static final String kLoggerTag = Util.classToJsonName(AlignDriveController.class);
	private final Limelight mLimelight = Limelight.getInstance();
	private final SynchronousPIDF mPidController = new SynchronousPIDF(0.0, 0.0, 0.0, 0.0);
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private MedianFilter mTargetGyroYawFilter = new MedianFilter(10);
	private int mTargetReadingsCount;
	private double mTargetGyroYaw;

	public AlignDriveController() {
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double gyroYawDegrees = state.driveYawDegrees;
		double gyroYawAngularVelocity = state.driveYawAngularVelocityDegrees;
		if (mLimelight.isTargetFound()) {
			double currentYawToTargetDegrees = mLimelight.getYawToTarget();
			LiveGraph.add("currentYawToTargetDegrees", currentYawToTargetDegrees);
			++mTargetReadingsCount;
			mTargetGyroYaw = mTargetGyroYawFilter.calculate(gyroYawDegrees - currentYawToTargetDegrees);
		}
		if (mTargetReadingsCount >= 1) {
			double absoluteGyroYawErrorDegrees = Math.abs(gyroYawDegrees - mTargetGyroYaw);
			if (absoluteGyroYawErrorDegrees > mVisionConfig.acceptableYawError) {
				var preciseGains = mVisionConfig.preciseGains;
				mPidController.setPID(preciseGains.p, preciseGains.i, preciseGains.d);
				mPidController.setSetpoint(mTargetGyroYaw);
				double percentOutput = mPidController.calculate(gyroYawDegrees, gyroYawAngularVelocity);
				double staticAdjustedPercentOutput = percentOutput + Math.signum(percentOutput) * mConfig.turnGainsS;
//				LiveGraph.add("percentOutput", percentOutput);
//				LiveGraph.add("gyroYawDegrees", gyroYawDegrees);
//				LiveGraph.add("gyroYawAngularVelocity", gyroYawAngularVelocity);
//				LiveGraph.add("mTargetGyroYaw", mTargetGyroYaw);
//				LiveGraph.add("staticAdjustedPercentOutput", staticAdjustedPercentOutput);
//				LiveGraph.add("isDone", 0.0);
				mOutputs.leftOutput.setPercentOutput(-staticAdjustedPercentOutput);
				mOutputs.rightOutput.setPercentOutput(staticAdjustedPercentOutput);
				return;
			}
		}
//		LiveGraph.add("isDone", 1.0);

		super.updateSignal(commands, state);
	}
}
