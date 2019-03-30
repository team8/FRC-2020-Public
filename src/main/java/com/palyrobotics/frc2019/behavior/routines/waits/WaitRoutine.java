package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public abstract class WaitRoutine extends Routine {

    protected boolean isDone;

    @Override
    public final void start() {}

    public abstract boolean isCompleted();

    @Override
    public final Commands update(Commands commands) {
        this.isDone = isCompleted();
        return commands;
    }

    @Override
    public final Commands cancel(Commands commands) {
//        System.out.println("Done");
        return commands;
    }

    @Override
    public final boolean finished() {
        return this.isDone;
    }

    @Override
    public abstract Subsystem[] getRequiredSubsystems();

    @Override
    public abstract String getName();
}
