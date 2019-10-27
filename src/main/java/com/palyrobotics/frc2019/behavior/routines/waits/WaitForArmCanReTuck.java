package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.subsystems.Subsystem;

public class WaitForArmCanReTuck extends WaitRoutine {
    @Override
    public boolean isCompleted() {
        return mRobotState.elevatorPosition < 0.3;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mIntake};
    }

    @Override
    public String getName() {
        return "Can Tuck Routine";
    }
}
