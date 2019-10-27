package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.subsystems.Subsystem;

public class WaitForCargoGroundIntake extends WaitRoutine {

    @Override
    public boolean isCompleted() {
        return mRobotState.hasIntakeCargo;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mIntake};
    }

    @Override
    public String getName() {
        return null;
    }
}
