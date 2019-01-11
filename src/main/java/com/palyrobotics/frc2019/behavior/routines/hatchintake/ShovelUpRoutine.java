package com.palyrobotics.frc2019.behavior.routines.hatchintake;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Shovel;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class ShovelUpRoutine extends Routine{

    private boolean alreadyRan;

    @Override
    public void start() {
        alreadyRan = false;
    }
    @Override
    public Commands update(Commands commands) {
        commands.wantedShovelUpDownState = Shovel.UpDownState.UP;
        alreadyRan = true;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean finished() {
        return alreadyRan;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { shovel };
    }

    @Override
    public String getName() {
        return "ShovelUpRoutine";
    }
}
