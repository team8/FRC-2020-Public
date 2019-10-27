package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class CascadingGyroEncoderTurnAngleRoutine extends Routine {

    private double angle;

    public CascadingGyroEncoderTurnAngleRoutine(double angle) {
        this.angle = angle;
    }

    @Override
    public void start() {
        mDrive.setCascadingGyroEncoderTurnAngleController(angle);
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mDrive.controllerOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {mDrive};
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
