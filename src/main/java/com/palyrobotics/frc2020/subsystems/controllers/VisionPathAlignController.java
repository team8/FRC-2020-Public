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
	private Pose2d mOrigin = new Pose2d(0, 0, Rotation2d.fromDegrees(0));
	private Timer mTimer = new Timer();
	private Trajectory mTrajectory = null;
	private int mCounter = 0;

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (mCounter++ > mConfig.trajectoryUpdateCycle) { // every 5 cycles reset trajectory to ensure accuracy.
			mWaypoints.add(mOrigin);
			mWaypoints.add(new Pose2d(Units.inchesToMeters(mLimelight.getPnPTranslationX()),
					Units.inchesToMeters(mLimelight.getPnPTranslationY()),
					Rotation2d.fromDegrees(mLimelight.getYawToTarget())));
			mTrajectory = TrajectoryGenerator.generateTrajectory(mWaypoints,
					DrivetrainConstants.getStandardTrajectoryConfig());
			mCounter = 0;
			mWaypoints.clear();
			mTimer.reset();
		}
		getDriveOutputFromTrajectory(mDriveOutputs, mTrajectory, mTimer.get());
	}
}
