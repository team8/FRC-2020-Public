package com.palyrobotics.frc2019.behavior.routines.intake;

import com.palyrobotics.frc2019.behavior.OneTimeRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class IntakeLevelOneRocketRoutine extends OneTimeRoutine {
    @Override
    public Commands doOnce(Commands commands) {
        commands.wantedIntakeState = Intake.IntakeMacroState.HOLDING_CARGO;
        return commands;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {mIntake};
    }

    @Override
    public String getName() {
        return "Intake Up Routine";
    }
}
