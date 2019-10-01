package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;

public abstract class Subsystem {

    private final String mName;

    public Subsystem(String name) {
        mName = name;
        reset();
    }

    public abstract void update(Commands commands, RobotState robotState);

    public String getConfigName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    public void start() {
        reset();
    }

    public void stop() {
        reset();
    }

    public void reset() {
    }

    public String getStatus() {
        return null;
    }
}