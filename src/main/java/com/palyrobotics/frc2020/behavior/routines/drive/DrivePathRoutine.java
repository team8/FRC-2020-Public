package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.config.Configs;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrivePathRoutine extends Routine {

    private final boolean mIsReversed;
    private final Trajectory mTrajectory;
    private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

    public DrivePathRoutine(boolean isReversed, Pose2d... waypoints) {
        mIsReversed = isReversed;
        mTrajectory = TrajectoryGenerator.generateTrajectory(
                Arrays.asList(waypoints),
                new TrajectoryConfig(mDriveConfig.maxPathVelocityMetersPerSecond, mDriveConfig.maxPathAccelerationMetersPerSecondSquared)
        );
    }

    public DrivePathRoutine(Pose2d... waypoints) {
        this(false, waypoints);
    }

    @Override
    public void start() {
        mDrive.setTrajectoryController(mIsReversed, mTrajectory);
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
        return false;
//         return mDrive.isOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }
}
