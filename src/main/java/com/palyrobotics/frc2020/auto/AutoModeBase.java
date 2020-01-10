package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.Routine;

public abstract class AutoModeBase {

    // To set the auto mode, set these variables in code!
    public static Alliance sAlliance = Alliance.BLUE;

    private boolean mIsActive;

    // Will be run before the routine is started
    public void preStart() {

    }

    public abstract Routine getRoutine();

    public void stop() {
        mIsActive = false;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public String getKey() {
        return getClass().getSimpleName();
    }

    public enum Alliance {
        RED,
        BLUE
    }
}
