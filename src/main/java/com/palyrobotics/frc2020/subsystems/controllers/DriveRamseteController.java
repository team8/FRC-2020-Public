package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.control.DriveOutputs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DriveRamseteController extends Drive.DriveController {

	public static final double B = 2.0, ZETA = 0.7;

	private final RamseteController mController;

	public DriveRamseteController() {
		mController = new RamseteController(B, ZETA);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		getDriveOutputFromTrajectory(mDriveOutputs, commands.getDriveWantedTrajectory(),
				commands.getDriveWantedTrajectoryTimeSeconds());
	}

	void getDriveOutputFromTrajectory(DriveOutputs driveOutputs, Trajectory trajectory, double sampleTime) {
		Trajectory.State targetPose = trajectory.sample(sampleTime);
		ChassisSpeeds speeds = mController.calculate(RobotState.getInstance().drivePose, targetPose);
		DifferentialDriveWheelSpeeds wheelSpeeds = DrivetrainConstants.kKinematics.toWheelSpeeds(speeds);
		driveOutputs.leftOutput.setTargetVelocityProfiled(wheelSpeeds.leftMetersPerSecond * 60.0,
				mDriveConfig.profiledVelocityGains);
		driveOutputs.rightOutput.setTargetVelocityProfiled(wheelSpeeds.rightMetersPerSecond * 60.0,
				mDriveConfig.profiledVelocityGains);

		CSVWriter.addData("targetLeftVelocity", wheelSpeeds.leftMetersPerSecond);
		CSVWriter.addData("targetRightVelocity", wheelSpeeds.rightMetersPerSecond);
		CSVWriter.addData("currentPoseX", RobotState.getInstance().drivePose.getTranslation().getX());
		CSVWriter.addData("currentPoseY", RobotState.getInstance().drivePose.getTranslation().getY());
		CSVWriter.addData("leftVelocity", RobotState.getInstance().driveLeftVelocity);
		CSVWriter.addData("rightVelocity", RobotState.getInstance().driveRightVelocity);
		CSVWriter.addData("targetPoseX", targetPose.poseMeters.getTranslation().getX());
		CSVWriter.addData("targetPoseY", targetPose.poseMeters.getTranslation().getY());
	}
}
