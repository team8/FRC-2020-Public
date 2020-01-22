package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.vision.Limelight;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.util.Units;

import java.util.ArrayList;
import java.util.List;

public class VisionPathAlignController extends Drive.DriveController {

	public static final double B = 2.0, ZETA = 0.7;
	private Limelight mLimelight = Limelight.getInstance();
	private List<Pose2d> mWaypoints = new ArrayList<>();
	private Pose2d mOrigin = new Pose2d(0, 0, Rotation2d.fromDegrees(0));
	private final RamseteController mController;
	private Timer mTimer = new Timer();
	private Trajectory mTrajectory = null;
	private int mCounter = 0;

	public VisionPathAlignController() {
		mController = new RamseteController(B, ZETA);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if(mCounter > 5) { //every 5 cycles reset trajectory and all related variables to ensure accuracy.
			mWaypoints.add(mOrigin);
			mWaypoints.add(new Pose2d(Units.inchesToMeters(mLimelight.getPnPTranslationX()),
					Units.inchesToMeters(mLimelight.getPnPTranslationY()),
					Rotation2d.fromDegrees(mLimelight.getYawToTarget())));
			mTrajectory = TrajectoryGenerator.generateTrajectory(mWaypoints, DrivetrainConstants.getStandardTrajectoryConfig());
			mCounter = 0;
			mWaypoints.clear();
			mTimer.reset();
		}
		Trajectory.State targetPose = mTrajectory.sample(mTimer.get());
		ChassisSpeeds speeds = mController.calculate(state.drivePose, targetPose);
		DifferentialDriveWheelSpeeds wheelSpeeds = DrivetrainConstants.kKinematics.toWheelSpeeds(speeds);
		mDriveOutputs.leftOutput.setTargetVelocityProfiled(wheelSpeeds.leftMetersPerSecond * 60.0,
				mDriveConfig.profiledVelocityGains);
		mDriveOutputs.rightOutput.setTargetVelocityProfiled(wheelSpeeds.rightMetersPerSecond * 60.0,
				mDriveConfig.profiledVelocityGains);
		mCounter++;
	}
}
