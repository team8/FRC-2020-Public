package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DrivePathRoutine extends Routine {

    private final Trajectory mTrajectory;

    public DrivePathRoutine(Trajectory trajectory) {
        mTrajectory = trajectory;
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
}
