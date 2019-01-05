package com.palyrobotics.frc2018.behavior.routines.intake;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Intake;
import com.palyrobotics.frc2018.subsystems.Subsystem;

/**
 * @author Justin
 */
public class IntakeCloseRoutine extends Routine {

    private boolean alreadyRan;

    @Override
    public void start() {
        alreadyRan = false;
    }
    
    @Override
    public Commands update(Commands commands) {
        commands.wantedIntakeOpenCloseState = Intake.OpenCloseState.CLOSED;
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
        return "IntakeCloseRoutine";
    }
}
