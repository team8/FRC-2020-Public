package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        List<Pose2d> generatorPoints;
        TrajectoryConfig trajectoryConfig;
        if (isReversed) {
            // We need to clone waypoints since reversing is in-place and we don't want to modify passed in list
            generatorPoints = new ArrayList<>(waypoints);
            Collections.reverse(generatorPoints);
            trajectoryConfig = DrivetrainConstants.kReverseTrajectoryConfig;
        } else {
            generatorPoints = waypoints;
            trajectoryConfig = DrivetrainConstants.kTrajectoryConfig;
        }
        mTrajectory = TrajectoryGenerator.generateTrajectory(generatorPoints, trajectoryConfig);
    }

    @Override
    public void start() {

    }

    @Override
    public Commands update(Commands commands) {
        commands.setDriveFollowPath(mTrajectory);
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.setDriveNeutral();
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mDrive.isOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }
}
