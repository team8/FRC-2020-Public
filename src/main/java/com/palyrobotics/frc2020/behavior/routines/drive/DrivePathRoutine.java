package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.*;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

public class DrivePathRoutine extends TimeoutRoutineBase {

	public static class WaypointWithSpeed {

		public WaypointWithSpeed(double xInches, double yInches, double yawDegrees, double speed) {

		}
	}

	private static final double kTimeoutMultiplier = 1.0;
	private final List<Pose2d> mWaypoints;
	private final TrajectoryConfig mTrajectoryConfig = DriveConstants.getStandardTrajectoryConfig();
	private boolean mShouldReversePath;
	private Trajectory mTrajectory;

	/**
	 * @param waypoints Points to move towards from current pose. No initial pose needs to be supplied.
	 */
	public DrivePathRoutine(Pose2d... waypoints) {
		this(Arrays.asList(waypoints));
	}

	/**
	 * @see #DrivePathRoutine(Pose2d...)
	 */
	public DrivePathRoutine(List<Pose2d> waypoints) {
		mWaypoints = waypoints;
	}

	public DrivePathRoutine endingVelocity(double startingVelocityMetersPerSecond) {
		mTrajectoryConfig.setEndVelocity(startingVelocityMetersPerSecond);
		return this;
	}

	/**
	 * Robot will try to drive in reverse while traversing the path. Does not reverse the path itself.
	 */
	public DrivePathRoutine driveInReverse() {
		mTrajectoryConfig.setReversed(true);
		return this;
	}

	/**
	 * Reverse points in the path. Does not make the robot drive in reverse.
	 */
	public DrivePathRoutine reversePath() {
		mShouldReversePath = true;
		return this;
	}

	/**
	 * Reverses the path and attempts to drive it backwards. Useful for getting a robot back to its
	 * starting position after running a path.
	 */
	public DrivePathRoutine reverse() {
		driveInReverse();
		reversePath();
		return this;
	}

	public void generateTrajectory(Pose2d startingPose, double startingVelocityMetersPerSecond) {
		if (mTrajectory == null) {
			var waypointsWithStart = new LinkedList<>(mWaypoints);
			if (mShouldReversePath) {
				Collections.reverse(waypointsWithStart);
			}
			waypointsWithStart.addFirst(startingPose);
			mTrajectory = TrajectoryGenerator.generateTrajectory(waypointsWithStart, mTrajectoryConfig);
			mTimeout = mTrajectory.getTotalTimeSeconds() * kTimeoutMultiplier;
		} else {
			throw new IllegalStateException("Trajectory already generated!");
		}
	}

	public Trajectory getTrajectory() {
		return mTrajectory;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		// Required to start the timeout timer
		super.start(commands, state);
		generateTrajectory(state.drivePoseMeters, state.driveVelocityMetersPerSecond);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		commands.setDriveFollowPath(mTrajectory);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		// TODO: possibly implement to see if we are within a tolerance of the end early
		return false;
	}
}
