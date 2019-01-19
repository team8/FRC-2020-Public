package com.palyrobotics.frc2019.behavior.routines.fingers;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class FingersExpelRoutine extends Routine {

    private Fingers.FingersState wantedFingersOpenCloseState;

    private double mTimeout;

    private long mStartTime;

    public FingersExpelRoutine(double timeout) {
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedFingersExpelState = Fingers.PushingState.EXPELLING;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedFingersExpelState = Fingers.PushingState.CLOSED;
        return commands;
    }

    @Override
    public boolean finished() {
        return System.currentTimeMillis() - mStartTime > mTimeout * 1000;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { fingers };
    }

    @Override
    public String getName() {
        return "FingersExpelRoutine";
    }
}