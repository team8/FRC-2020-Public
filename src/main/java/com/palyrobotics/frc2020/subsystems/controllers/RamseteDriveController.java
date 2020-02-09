package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class RamseteDriveController extends Drive.DriveController {

	public static final double kB = 2.1, kZeta = 0.8;

	private final Timer mTimer = new Timer();
	private RamseteController mController;
	private Trajectory mTrajectory;

	public RamseteDriveController() {
		mTimer.start();
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		Trajectory wantedTrajectory = commands.getDriveWantedTrajectory();
		if (mTrajectory != wantedTrajectory) {
			// We want our update function to define every state associated with
			// this controller, so we must take care to handle these internal states to
			// avoid errors and state bleeding.
			mTrajectory = wantedTrajectory;
			mController = new RamseteController(kB, kZeta);
			mTimer.reset();
		}
		Trajectory.State targetPose = wantedTrajectory.sample(mTimer.get());
		ChassisSpeeds speeds = mController.calculate(state.drivePose, targetPose);
		DifferentialDriveWheelSpeeds wheelSpeeds = DriveConstants.kKinematics.toWheelSpeeds(speeds);
		// TODO: remove 60
		mOutputs.leftOutput.setTargetVelocityProfiled(wheelSpeeds.leftMetersPerSecond,
				mConfig.profiledVelocityGains);
		mOutputs.rightOutput.setTargetVelocityProfiled(wheelSpeeds.rightMetersPerSecond,
				mConfig.profiledVelocityGains);
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
