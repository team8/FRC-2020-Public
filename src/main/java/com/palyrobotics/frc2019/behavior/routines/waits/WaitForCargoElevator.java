package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.subsystems.Subsystem;

public class WaitForCargoElevator extends WaitRoutine {

    @Override
    public boolean isCompleted() {
        return robotState.hasPusherCargo;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{elevator};
    }

    @Override
    public String getName() {
        return null;
    }
}
