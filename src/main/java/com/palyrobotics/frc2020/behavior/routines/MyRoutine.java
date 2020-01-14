package com.palyrobotics.frc2020.behavior.routines;

import com.palyrobotics.frc2020.behavior.DoOnceRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

import java.util.Set;

public class MyRoutine extends DoOnceRoutine {

    @Override
    protected void doOnce(Commands commands) {

    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return Set.of(mDrive, mDrive);
    }
}
