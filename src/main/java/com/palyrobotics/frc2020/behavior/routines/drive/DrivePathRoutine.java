package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.config.Configs;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

import java.util.Arrays;
import java.util.List;

public class DrivePathRoutine extends Routine {

    private final Trajectory mTrajectory;
    private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
    private List<Pose2d> mPoseList;

    public DrivePathRoutine(List<Pose2d> waypoints) {
        this(waypoints.toArray(new Pose2d[0]));
    }

    public DrivePathRoutine(boolean isReversed, List<Pose2d> waypoints) {
        this(isReversed, waypoints.toArray(new Pose2d[0]));
    }

    public DrivePathRoutine(boolean isReversed, Pose2d... waypoints) {
        mTrajectory = TrajectoryGenerator.generateTrajectory(
                Arrays.asList(waypoints),
                isReversed ? DrivetrainConstants.kReverseTrajectoryConfig : DrivetrainConstants.kTrajectoryConfig
        );
        mPoseList = Arrays.asList(waypoints);
    }

    public DrivePathRoutine(Pose2d... waypoints) {
        this(false, waypoints);
    }

    @Override
    public void start() {
        mDrive.setTrajectoryController(mTrajectory);
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        mDrive.setNeutral();
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
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

    @Override
    public String toString() {
        return mPoseList.toString();
    }
}
