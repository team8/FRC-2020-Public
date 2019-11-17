package com.palyrobotics.frc2019.behavior.routines.fingers;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import edu.wpi.first.wpilibj.Timer;

public class FingersCycleRoutine extends Routine {

    private boolean mAlreadyRan;
    private double mTimeout;
    private final Timer mTimer = new Timer();

    public FingersCycleRoutine(double timeout) {
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mTimer.reset();
        mTimer.start();
        mAlreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedFingersOpenCloseState = Fingers.FingersState.CLOSE;
        commands.wantedFingersExpelState = Fingers.PushingState.CLOSED;
        if (mTimer.get() > mTimeout) {
            commands.wantedFingersOpenCloseState = Fingers.FingersState.OPEN;
            commands.wantedFingersExpelState = Fingers.PushingState.CLOSED;
            mAlreadyRan = true;
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mAlreadyRan;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mFingers};
    }

    @Override
    public String getName() {
        return "Fingers Close Routine";
    }
}