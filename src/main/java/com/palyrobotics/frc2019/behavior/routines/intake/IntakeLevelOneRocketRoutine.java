package com.palyrobotics.frc2019.behavior.routines.intake;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class IntakeLevelOneRocketRoutine extends Routine {
    private boolean alreadyRan;

    @Override
    public void start() {
        alreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedIntakeState = Intake.IntakeMacroState.EXPELLING_ROCKET;
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
        return new Subsystem[] { intake };
    }

    @Override
    public String getName() {
        return "IntakeUpRoutine";
    }
}
