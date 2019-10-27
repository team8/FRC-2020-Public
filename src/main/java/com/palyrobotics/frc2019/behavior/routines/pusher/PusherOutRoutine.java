package com.palyrobotics.frc2019.behavior.routines.pusher;

import com.palyrobotics.frc2019.behavior.OneTimeRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class PusherOutRoutine extends OneTimeRoutine {
    @Override
    public Commands doOnce(Commands commands) {
        commands.wantedPusherInOutState = Pusher.PusherState.OUT;
        return commands;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mPusher};
    }

    @Override
    public String getName() {
        return "Pusher Out Routine";
    }
}
