package com.palyrobotics.frc2020.subsystems.controllers.drive;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SynchronousPIDF;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.MedianFilter;

public class AlignDriveController extends ChezyDriveController {

	private static final String kLoggerTag = Util.classToJsonName(AlignDriveController.class);
	public static final int kFilterSize = 3;
	private final Limelight mLimelight = Limelight.getInstance();
	private final SynchronousPIDF mPidController = new SynchronousPIDF();
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private MedianFilter mTargetYawFilter = new MedianFilter(kFilterSize);
	private int mTargetFoundCount;
	private double mTargetGyroYaw;

	public AlignDriveController() {
	}

//	@Override
//	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
//		double gyroYawDegrees = state.driveYawDegrees;
//		double gyroYawAngularVelocity = state.driveYawAngularVelocityDegrees;
//		if (mLimelight.isTargetFound()) {
//			double visionYawToTargetDegrees = mLimelight.getYawToTarget();
//			LiveGraph.add("visionYawToTargetDegrees", visionYawToTargetDegrees);
//			mTargetGyroYaw = gyroYawDegrees - mVisionYawFilter.calculate(visionYawToTargetDegrees);
//			mTargetReadingsCount++;
//		} else {
//			mTargetReadingsCount = 0;
//		}
//		if (mTargetReadingsCount >= 10) {
//			var preciseGains = mVisionConfig.preciseGains;
//			mPidController.setPID(preciseGains.p, preciseGains.i, preciseGains.d);
////				mPidController.setIntegratorRange(-preciseGains.iMax, preciseGains.iMax);
//			mPidController.setSetpoint(mTargetGyroYaw);
//			double percentOutput = mPidController.calculate(gyroYawDegrees, -gyroYawAngularVelocity);
//			double staticAdjustedPercentOutput = percentOutput + Math.signum(percentOutput) * mConfig.turnGainsS;
//			LiveGraph.add("percentOutput", percentOutput);
//			LiveGraph.add("gyroYawDegrees", gyroYawDegrees);
//			LiveGraph.add("gyroYawAngularVelocity", gyroYawAngularVelocity);
//			LiveGraph.add("mTargetGyroYaw", mTargetGyroYaw);
//			LiveGraph.add("staticAdjustedPercentOutput", staticAdjustedPercentOutput);
//			LiveGraph.add("isDone", 0.0);
//			mOutputs.leftOutput.setPercentOutput(-staticAdjustedPercentOutput);
//			mOutputs.rightOutput.setPercentOutput(staticAdjustedPercentOutput);
//			return;
//		}
//		LiveGraph.add("isDone", 1.0);
//		super.updateSignal(commands, state);
//	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (state.driveIsGyroReady) {
			double gyroYawDegrees = state.driveYawDegrees;
			double gyroYawAngularVelocity = state.driveYawAngularVelocityDegrees;
			if (mLimelight.isTargetFound()) {
				double visionYawToTargetDegrees = mLimelight.getYawToTarget();
				mTargetGyroYaw = mTargetYawFilter.calculate(gyroYawDegrees - visionYawToTargetDegrees);
				mTargetFoundCount++;
			} else {
				mTargetFoundCount = 0;
			}
			if (mTargetFoundCount >= kFilterSize) {
				calculate(mTargetGyroYaw, gyroYawDegrees, -gyroYawAngularVelocity);
				return;
			}
		} else {
			if (mLimelight.isTargetFound()) {
				calculate(0.0, mLimelight.getYawToTarget(), null);
			}
			mTargetFoundCount = 0;
		}
		super.updateSignal(commands, state);
	}

	private void calculate(double targetDegrees, double degrees, Double degreesDerivative) {
		var preciseGains = mVisionConfig.preciseGains;
		mPidController.setPID(preciseGains.p, preciseGains.i, preciseGains.d);
		mPidController.setSetpoint(targetDegrees);
		double percentOutput = degreesDerivative == null ? mPidController.calculate(degrees) : mPidController.calculate(degrees, degreesDerivative);
		double turnGainS = mConfig.turnGainsS;
		if (Math.abs(mPidController.getError()) < mVisionConfig.acceptableYawError) turnGainS *= 0.2;
		double staticAdjustedPercentOutput = percentOutput + Math.signum(percentOutput) * turnGainS;
		mOutputs.leftOutput.setPercentOutput(-staticAdjustedPercentOutput);
		mOutputs.rightOutput.setPercentOutput(staticAdjustedPercentOutput);
	}
}
