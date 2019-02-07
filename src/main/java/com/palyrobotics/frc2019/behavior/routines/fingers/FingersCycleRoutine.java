package com.palyrobotics.frc2019.behavior.routines.fingers;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class FingersCycleRoutine extends Routine {

    private Fingers.FingersState wantedFingersOpenCloseState;

    private boolean alreadyRan;
    private double timeout;
    private double startTime;

    public FingersCycleRoutine(double timeout) {
        this.timeout = timeout * 1000;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        alreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedFingersOpenCloseState = Fingers.FingersState.CLOSE;
        commands.wantedFingersExpelState = Fingers.PushingState.EXPELLING;
        if(System.currentTimeMillis() > this.timeout + startTime) {
            commands.wantedFingersOpenCloseState = Fingers.FingersState.OPEN;
            commands.wantedFingersExpelState = Fingers.PushingState.CLOSED;
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
        return new Subsystem[] { fingers };
    }

    @Override
    public String getName() {
        return "FingersCloseRoutine";
    }
}