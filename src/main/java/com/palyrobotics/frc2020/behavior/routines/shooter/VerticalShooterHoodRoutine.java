package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.config.Commands;

public class VerticalShooterHoodRoutine extends SolenoidRoutine {

    public VerticalShooterHoodRoutine(boolean solenoidOutput) {
        super(solenoidOutput);
    }

    @Override
    protected Commands doOnce(Commands commands) {
        commands.verticalHoodSolenoidOutput = solenoidOutput;
        return commands;
    }
}
