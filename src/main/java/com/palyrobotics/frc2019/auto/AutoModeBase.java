package com.palyrobotics.frc2019.auto;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.AllianceDistances;
import com.palyrobotics.frc2019.util.config.Configs;

public abstract class AutoModeBase {
    private boolean mIsActive;

    public abstract String toString();

    // Will be run before the routine is taken
    public abstract void preStart();

    public AutoModeBase() {
        loadDistances();
    }

    public enum Alliance {
        RED,
        BLUE
    }

    // To set the auto mode, set these variables in code!
    public static Alliance sAlliance = Alliance.BLUE;
    public static AllianceDistances sDistances;

    private static void loadDistances() {
        sDistances = Configs.get(AllianceDistances.class, "Team8Field");
    }

    public abstract Routine getRoutine();

    public void stop() {
        mIsActive = false;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public abstract String getKey();
}