package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.AllianceDistances;
import com.palyrobotics.frc2020.util.config.Configs;

public abstract class AutoModeBase {

    // To set the auto mode, set these variables in code!
    public static Alliance sAlliance = Alliance.BLUE;
    public static AllianceDistances sDistances;
    private boolean mIsActive;

    public AutoModeBase() {
        loadDistances();
    }

    private static void loadDistances() {
        sDistances = Configs.get(AllianceDistances.class, "Team8Field");
    }

    public abstract String toString();

    // Will be run before the routine is taken
    public abstract void preStart();

    public abstract Routine getRoutine();

    public void stop() {
        mIsActive = false;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public abstract String getKey();

    public enum Alliance {
        RED,
        BLUE
    }
}
