package com.palyrobotics.frc2019.behavior.routines.pusher;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class PusherOutRoutine extends Routine {
    private boolean alreadyRan;

    @Override
    public void start() {
        alreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
//        if(robotState.hasPusherCargo) {
            commands.wantedPusherInOutState = Pusher.PusherState.OUT;
//        }
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
        return new Subsystem[] { pusher };
    }

    @Override
    public String getName() {
        return "PusherOutRoutine";
    }
}
