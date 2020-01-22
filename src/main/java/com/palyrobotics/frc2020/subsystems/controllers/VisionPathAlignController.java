package com.palyrobotics.frc2020.subsystems.controllers;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.util.Units;

public class VisionPathAlignController extends DriveRamseteController {

	private Limelight mLimelight = Limelight.getInstance();
	private final DriveConfig mConfig = Configs.get(DriveConfig.class);

	private List<Pose2d> mWaypoints = new ArrayList<>();
	private Pose2d mOrigin = new Pose2d();
	private Timer mTimer = new Timer();
	private Trajectory mTrajectory;
	private int mCounter;
	private Boolean mFirstTimeRun = true;

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		// TODO: generation of new trajectory should be based on actual error from
		// limelight and current pose
		if (mCounter++ > mConfig.trajectoryUpdateCycle || mFirstTimeRun) {
			// TODO: odometry needs to be reset. add a boolean in commands,
			// wantsOdometryReset
			mWaypoints.add(RobotState.getInstance().drivePose);
			mWaypoints.add(new Pose2d(Units.inchesToMeters(mLimelight.getPnPTranslationX()),
					Units.inchesToMeters(mLimelight.getPnPTranslationY()),
					Rotation2d.fromDegrees(mLimelight.getYawToTarget())));
			mTrajectory = TrajectoryGenerator.generateTrajectory(mWaypoints,
					DrivetrainConstants.getStandardTrajectoryConfig());
			mCounter = 0;
			mWaypoints.clear();
			mTimer.reset();
			mFirstTimeRun = false;
		}
		setDriveOutputFromTrajectory(mTrajectory, mTimer.get());
	}
}
