package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;

import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DriveRamseteController extends Drive.DriveController {

	public static final double kB = 2.0, kZeta = 0.7;

	private final RamseteController mController;

	public DriveRamseteController() {
		mController = new RamseteController(kB, kZeta);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		Trajectory.State targetPose = commands.getDriveWantedTrajectory()
				.sample(commands.getDriveWantedTrajectoryTimeSeconds());
		ChassisSpeeds speeds = mController.calculate(state.drivePose, targetPose);
		DifferentialDriveWheelSpeeds wheelSpeeds = DriveConstants.kKinematics.toWheelSpeeds(speeds);
		mDriveOutputs.leftOutput.setTargetVelocityProfiled(wheelSpeeds.leftMetersPerSecond,
				mDriveConfig.profiledVelocityGains);
		mDriveOutputs.rightOutput.setTargetVelocityProfiled(wheelSpeeds.rightMetersPerSecond,
				mDriveConfig.profiledVelocityGains);
		CSVWriter.addData("targetLeftVelocity", wheelSpeeds.leftMetersPerSecond);
		CSVWriter.addData("targetRightVelocity", wheelSpeeds.rightMetersPerSecond);
		CSVWriter.addData("currentPoseX", state.drivePose.getTranslation().getX());
		CSVWriter.addData("currentPoseY", state.drivePose.getTranslation().getY());
		CSVWriter.addData("leftVelocity", state.driveLeftVelocity);
		CSVWriter.addData("rightVelocity", state.driveRightVelocity);
		CSVWriter.addData("targetPoseX", targetPose.poseMeters.getTranslation().getX());
		CSVWriter.addData("targetPoseY", targetPose.poseMeters.getTranslation().getY());
	}
}
