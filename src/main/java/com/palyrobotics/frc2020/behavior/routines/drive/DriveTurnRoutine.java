package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

import java.util.Set;

public class DriveTurnRoutine extends Routine {

    @Override
    protected void update(Commands commands) {
        commands.setDriveTurn(0.0);
    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return Set.of(mDrive);
    }
}
