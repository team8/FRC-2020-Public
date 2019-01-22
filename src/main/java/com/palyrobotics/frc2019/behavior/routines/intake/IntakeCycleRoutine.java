package com.palyrobotics.frc2019.behavior.routines.intake;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class IntakeCycleRoutine extends Routine {
    private boolean alreadyRan;

    @Override
    public void start() {
        alreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
        if (!robotState.hasCargo) {
            commands.wantedIntakeUpDownState = Intake.UpDownState.HOLD;
            commands.wantedIntakingState = Intake.WheelState.INTAKING;
        } else {
            commands.wantedIntakeUpDownState = Intake.UpDownState.UP;
        }

        if(intake.getUpDownState() == Intake.UpDownState.UP) {
            commands.wantedIntakingState = Intake.WheelState.INTAKING;
            alreadyRan = true;
        }
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
        return "IntakeCycleRoutine";
    }
}