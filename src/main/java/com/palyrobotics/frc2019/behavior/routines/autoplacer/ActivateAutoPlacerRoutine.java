package com.palyrobotics.frc2019.behavior.routines.autoplacer;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class ActivateAutoPlacerRoutine extends Routine {

    private boolean alreadyRan;

    @Override
    public void start() {
        alreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
        commands.autoPlacerOutput = true;

        alreadyRan = true;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean finished() {
        return alreadyRan;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { autoPlacer };
    }

    @Override
    public String getName() {
        return "ActivateAutoPlacerRoutine";
    }
}