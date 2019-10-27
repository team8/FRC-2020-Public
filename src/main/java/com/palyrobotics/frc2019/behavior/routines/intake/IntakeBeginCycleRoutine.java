package com.palyrobotics.frc2019.behavior.routines.intake;

import com.palyrobotics.frc2019.behavior.OneTimeRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class IntakeBeginCycleRoutine extends OneTimeRoutine {
    @Override
    public Commands doOnce(Commands commands) {
        commands.wantedIntakeState = Intake.IntakeMacroState.GROUND_INTAKE;
        commands.addWantedRoutine(new PusherInRoutine());
        return commands;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mIntake};
    }

    @Override
    public String getName() {
        return "Intake Cycle Routine";
    }
}