package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public abstract class Subsystem {

    private final String mName;

    public Subsystem(String name) {
        mName = name;
    }

    public abstract void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState);

    public String getConfigName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getStatus() {
        return null;
    }
}