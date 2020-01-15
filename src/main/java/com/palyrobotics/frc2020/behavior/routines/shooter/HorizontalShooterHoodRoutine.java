package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class HorizontalShooterHoodRoutine extends SolenoidRoutine {

    public HorizontalShooterHoodRoutine(boolean solenoidOutput) {
        super(solenoidOutput);
    }

    @Override
    protected Commands doOnce(Commands commands) {
        commands.horizontalHoodSolenoidOutput = solenoidOutput;
        return commands;
    }
}
