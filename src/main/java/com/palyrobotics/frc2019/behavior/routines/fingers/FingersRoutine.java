package com.palyrobotics.frc2019.behavior.routines.fingers;

import com.palyrobotics.frc2019.behavior.OneTimeRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class FingersRoutine extends OneTimeRoutine {

    private Fingers.FingersState mFingerState;

    public FingersRoutine(Fingers.FingersState fingerState) {
        mFingerState = fingerState;
    }

    @Override
    public Commands doOnce(Commands commands) {
        commands.wantedFingersOpenCloseState = mFingerState;
        return commands;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {mFingers};
    }

    @Override
    public String getName() {
        return "Fingers Close Routine";
    }
}