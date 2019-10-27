package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public abstract class WaitRoutine extends Routine {

    private boolean mIsDone;

    @Override
    public final void start() {}

    public abstract boolean isCompleted();

    @Override
    public final Commands update(Commands commands) {
        mIsDone = isCompleted();
        return commands;
    }

    @Override
    public final Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public final boolean isFinished() {
        return mIsDone;
    }

    @Override
    public abstract Subsystem[] getRequiredSubsystems();

    @Override
    public abstract String getName();
}
