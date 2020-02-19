package com.palyrobotics.frc2020.subsystems.controllers.drive;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.MedianFilter;
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
	private MedianFilter mTargetGyroYawFilter = new MedianFilter(10);
	private int mTargetReadingsCount;
	private double mTargetGyroYaw;

	public AlignDriveController() {
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
//		if (mLimelight.isTargetFound()) {
//			double currentYawToTargetDegrees = mLimelight.getYawToTarget();
//			{
//				double yawToTargetDegrees = mYawReadings.calculate(currentYawToTargetDegrees);
//				if (++mYawReadingsCount == 10) {
//					mTargetGyroYaw = state.driveYawDegrees - yawToTargetDegrees;
//					Log.debug(kLoggerTag, String.format("Reset profiled PID controller: %f, %f", state.driveYawDegrees, yawToTargetDegrees));
//					mProfiledController.reset(state.driveYawDegrees);
//				}
//			}
//			if (mYawReadingsCount >= 10) {
//				double absoluteYawErrorDegrees = Math.abs(state.driveYawDegrees - mTargetGyroYaw);
//				if (absoluteYawErrorDegrees > mVisionConfig.acceptableYawError) {
//					double percentOutput;
//					if (absoluteYawErrorDegrees > 1.5) {
//						var profiledGains = mVisionConfig.profiledGains;
//						mProfiledController.setPID(profiledGains.p, profiledGains.i, profiledGains.d);
//						mProfiledController.setIntegratorRange(-profiledGains.iMax, profiledGains.iMax);
//						mProfiledController.setConstraints(new TrapezoidProfile.Constraints(profiledGains.velocity, profiledGains.acceleration));
//						percentOutput = mProfiledController.calculate(state.driveYawDegrees, mTargetGyroYaw);
//						TrapezoidProfile.State setpoint = mProfiledController.getSetpoint();
//						LiveGraph.add("target", setpoint.position);
//						LiveGraph.add("current", state.driveYawDegrees);
//						double targetVelocity = setpoint.velocity;
//						percentOutput += Math.signum(targetVelocity) * mConfig.turnGainsS + targetVelocity * profiledGains.f;
//					} else {
//						Log.info(kLoggerTag, "Using precise gains");
//						var preciseGains = mVisionConfig.preciseGains;
//						mPidController.setPID(preciseGains.p, preciseGains.i, preciseGains.d);
//						mPidController.setIntegratorRange(-preciseGains.iMax, preciseGains.iMax);
//						percentOutput = mPidController.calculate(currentYawToTargetDegrees) + Math.signum(-currentYawToTargetDegrees) * mConfig.turnGainsS;
//						mNeedsProfiledReset = true;
//					}
//					mOutputs.leftOutput.setPercentOutput(-percentOutput);
//					mOutputs.rightOutput.setPercentOutput(percentOutput);
//				} else {
//					mOutputs.leftOutput.setIdle();
//					mOutputs.rightOutput.setIdle();
//					mNeedsProfiledReset = true;
//				}
//				return;
//			}

//		double yawToTargetDegrees = mLimelight.getYawToTarget();
//		double absoluteYawErrorDegrees = Math.abs(yawToTargetDegrees);
//		if (absoluteYawErrorDegrees > mVisionConfig.acceptableYawError) {
//			var preciseGains = mVisionConfig.preciseGains;
//			mPidController.setPID(preciseGains.p, preciseGains.i, preciseGains.d);
//			mPidController.setIntegratorRange(-preciseGains.iMax, preciseGains.iMax);
//			double percentOutput = mPidController.calculate(yawToTargetDegrees, 0.0);
//			double staticAdjustedPercentOutput = percentOutput + Math.signum(percentOutput) * mConfig.turnGainsS;
//			CSVWriter.addData("percentOutput", percentOutput);
//			CSVWriter.addData("yawToTargetDegrees", yawToTargetDegrees);
//			CSVWriter.addData("staticAdjustedPercentOutput", staticAdjustedPercentOutput);
//			CSVWriter.addData("isDone", 0.0);
//			mOutputs.leftOutput.setPercentOutput(-staticAdjustedPercentOutput);
//			mOutputs.rightOutput.setPercentOutput(staticAdjustedPercentOutput);
//			return;
//		}
//		CSVWriter.addData("isDone", 1.0);
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
				mPidController.setIntegratorRange(-preciseGains.iMax, preciseGains.iMax);
				double percentOutput = mPidController.calculate(gyroYawDegrees, mTargetGyroYaw);
				double staticAdjustedPercentOutput = percentOutput + Math.signum(percentOutput) * mConfig.turnGainsS;
//				CSVWriter.addData("percentOutput", percentOutput);
//				CSVWriter.addData("gyroYawDegrees", gyroYawDegrees);
//				CSVWriter.addData("currentYawToTargetDegrees", currentYawToTargetDegrees);
//				CSVWriter.addData("gyroYawAngularVelocity", gyroYawAngularVelocity);
//				CSVWriter.addData("mTargetGyroYaw", mTargetGyroYaw);
//				CSVWriter.addData("staticAdjustedPercentOutput", staticAdjustedPercentOutput);
//				CSVWriter.addData("isDone", 0.0);
				LiveGraph.add("percentOutput", percentOutput);
				LiveGraph.add("gyroYawDegrees", gyroYawDegrees);
				LiveGraph.add("gyroYawAngularVelocity", gyroYawAngularVelocity);
				LiveGraph.add("mTargetGyroYaw", mTargetGyroYaw);
				LiveGraph.add("staticAdjustedPercentOutput", staticAdjustedPercentOutput);
				LiveGraph.add("isDone", 0.0);
				mOutputs.leftOutput.setPercentOutput(-staticAdjustedPercentOutput);
				mOutputs.rightOutput.setPercentOutput(staticAdjustedPercentOutput);
				return;
			}
		}
		LiveGraph.add("isDone", 1.0);

		super.updateSignal(commands, state);
	}

//	private static final double kMaxAngularPower = 0.4;
//	private final Limelight mLimelight = Limelight.getInstance();
//	private final PIDController mPidController = new PIDController(0.0, 0.0, 0.0);
//	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
//
//	@Override
//	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
//		if (mLimelight.isTargetFound()) {
//			if (Math.abs(mLimelight.getYawToTarget()) > mVisionConfig.acceptableYawError) {
//				if (mLimelight.getPipeline() == 0) {
//					mPidController.setPID(mVisionConfig.oneTimesZoomGains.p, mVisionConfig.oneTimesZoomGains.i, mVisionConfig.oneTimesZoomGains.d);
//					mPidController.setIntegratorRange(-mVisionConfig.oneTimesZoomGains.iMax, mVisionConfig.oneTimesZoomGains.iMax);
//				} else if (mLimelight.getPipeline() == 1) {
//					mPidController.setPID(mVisionConfig.twoTimesZoomGains.p, mVisionConfig.twoTimesZoomGains.i, mVisionConfig.twoTimesZoomGains.d);
//					mPidController.setIntegratorRange(-mVisionConfig.twoTimesZoomGains.iMax, mVisionConfig.twoTimesZoomGains.iMax);
//				}
//				double yawToTarget = mLimelight.getYawToTarget();
//				double percentOutput = mPidController.calculate(yawToTarget);
//				percentOutput = Util.clamp(percentOutput, -kMaxAngularPower, kMaxAngularPower);
//				double feedForward = Math.signum(-yawToTarget) * mConfig.turnGainsS;
//				double rightPercentOutput = percentOutput + feedForward, leftPercentOutput = -percentOutput - feedForward;
//				mOutputs.leftOutput.setPercentOutput(leftPercentOutput);
//				mOutputs.rightOutput.setPercentOutput(rightPercentOutput);
//			} else {
//				mOutputs.leftOutput.setIdle();
//				mOutputs.rightOutput.setIdle();
//			}
//		} else {
//			super.updateSignal(commands, state);
//		}
//	}
}
