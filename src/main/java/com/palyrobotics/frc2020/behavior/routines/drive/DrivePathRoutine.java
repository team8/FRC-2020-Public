package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.*;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

public class DrivePathRoutine extends TimeoutRoutineBase {

	private final Trajectory mTrajectory;

	public DrivePathRoutine(Pose2d... waypoints) {
		this(Arrays.asList(waypoints));
	}

	public DrivePathRoutine(List<Pose2d> waypoints) {
		this(false, waypoints);
	}

	public DrivePathRoutine(boolean isReversed, List<Pose2d> waypoints) {
		mTrajectory = TrajectoryGenerator.generateTrajectory(getGenerationPoints(isReversed, waypoints),
				getGenerationConfig(isReversed));
		mTimeout = mTrajectory.getTotalTimeSeconds();
	}

	private <T> List<T> getGenerationPoints(boolean isReversed, List<T> waypoints) {
		List<T> generatorPoints;
		if (isReversed) {
			// We need to clone waypoints since reversing is in-place and we don't want to
			// modify passed in list
			generatorPoints = new ArrayList<>(waypoints);
			Collections.reverse(generatorPoints);
		} else {
			generatorPoints = waypoints;
		}
		return generatorPoints;
	}

	private TrajectoryConfig getGenerationConfig(boolean isReversed) {
		TrajectoryConfig config = DrivetrainConstants.getStandardTrajectoryConfig();
		return isReversed ? config.setReversed(true) : config;
	}

	public DrivePathRoutine(boolean isReversed, Pose2d... waypoints) {
		this(isReversed, Arrays.asList(waypoints));
	}

	public DrivePathRoutine(Pose2d start, List<Translation2d> interiorWaypoints, Pose2d end) {
		this(false, start, interiorWaypoints, end);
	}

	public DrivePathRoutine(boolean isReversed, Pose2d start, List<Translation2d> interiorWaypoints, Pose2d end) {
		mTrajectory = TrajectoryGenerator.generateTrajectory(isReversed ? end : start,
				getGenerationPoints(isReversed, interiorWaypoints), isReversed ? start : end,
				getGenerationConfig(isReversed));
		mTimeout = mTrajectory.getTotalTimeSeconds();
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveFollowPath(mTrajectory, mTimer.get());
	}

	@Override
	public boolean checkIfFinishedEarly() {
		return false;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
