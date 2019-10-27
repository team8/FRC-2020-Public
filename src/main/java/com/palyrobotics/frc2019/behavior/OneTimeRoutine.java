package com.palyrobotics.frc2019.behavior;

import com.palyrobotics.frc2019.config.Commands;

public abstract class OneTimeRoutine extends Routine {

    private boolean mAlreadyRan;

    @Override
    public final void start() {
        mAlreadyRan = false;
    }

    @Override
    public final Commands update(Commands commands) {
        mAlreadyRan = true;
        return doOnce(commands);
    }

    protected abstract Commands doOnce(Commands commands);

    @Override
    public final Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public final boolean isFinished() {
        return mAlreadyRan;
    }
}
