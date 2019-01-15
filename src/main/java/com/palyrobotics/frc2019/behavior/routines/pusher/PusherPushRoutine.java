package com.palyrobotics.frc2019.behavior.routines.pusher;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;

public class PusherPushRoutine extends Routine {
    private Pusher.PusherState wantedPusherState;

    private boolean alreadyRan;

    public PusherPushRoutine(Pusher.PusherState wantedPusherState) {
        this.wantedPusherState = wantedPusherState;
    }

    @Override
    public void start() { alreadyRan = false; }

    @Override
    public Commands update(Commands commands) {
        if(wantedPusherState == Pusher.PusherState.IN && robotState.hasPusherCargo) {
            commands.wantedPusherInOutState = Pusher.PusherState.OUT;
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
        return new Subsystem[] { pusher };
    }

    @Override
    public String getName() {
        return "PusherPushRoutine";
    }
}
