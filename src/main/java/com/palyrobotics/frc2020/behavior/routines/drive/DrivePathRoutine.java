package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

import java.util.*;

public class DrivePathRoutine extends Routine {

    private final Trajectory mTrajectory;

    public DrivePathRoutine(Pose2d... waypoints) {
        this(Arrays.asList(waypoints));
    }

    public DrivePathRoutine(boolean isReversed, Pose2d... waypoints) {
        this(isReversed, Arrays.asList(waypoints));
    }

    public DrivePathRoutine(List<Pose2d> waypoints) {
        this(false, waypoints);
    }

    public DrivePathRoutine(boolean isReversed, List<Pose2d> waypoints) {
        mTrajectory = TrajectoryGenerator.generateTrajectory(
                getGenerationPoints(isReversed, waypoints),
                getGenerationConfig(isReversed)
        );
    }

    public DrivePathRoutine(Pose2d start, List<Translation2d> interiorWaypoints, Pose2d end) {
        this(false, start, interiorWaypoints, end);
    }

    public DrivePathRoutine(boolean isReversed, Pose2d start, List<Translation2d> interiorWaypoints, Pose2d end) {
        mTrajectory = TrajectoryGenerator.generateTrajectory(
                isReversed ? end : start,
                getGenerationPoints(isReversed, interiorWaypoints),
                isReversed ? start : end,
                getGenerationConfig(isReversed)
        );
    }

    private TrajectoryConfig getGenerationConfig(boolean isReversed) {
        TrajectoryConfig config = DrivetrainConstants.getStandardTrajectoryConfig();
        return isReversed ? config.setReversed(true) : config;
    }

    private <T> List<T> getGenerationPoints(boolean isReversed, List<T> waypoints) {
        List<T> generatorPoints;
        if (isReversed) {
            // We need to clone waypoints since reversing is in-place and we don't want to modify passed in list
            generatorPoints = new ArrayList<>(waypoints);
            Collections.reverse(generatorPoints);
        } else {
            generatorPoints = waypoints;
        }
        return generatorPoints;
    }

    @Override
    public void update(Commands commands) {
        commands.setDriveFollowPath(mTrajectory);
    }

    @Override
    public boolean checkFinished() {
        return mDrive.isOnTarget();
    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return Set.of(mDrive);
    }
}
