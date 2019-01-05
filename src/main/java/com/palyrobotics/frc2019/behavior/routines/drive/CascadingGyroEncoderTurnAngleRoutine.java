package com.palyrobotics.frc2018.behavior.routines.drive;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.subsystems.Subsystem;

public class CascadingGyroEncoderTurnAngleRoutine extends Routine {

    private double angle;

    public CascadingGyroEncoderTurnAngleRoutine(double angle) {
        this.angle = angle;
    }

    @Override
    public void start() {
        drive.setCascadingGyroEncoderTurnAngleController(angle);
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
    public boolean finished() {
        return drive.controllerOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {drive};
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
