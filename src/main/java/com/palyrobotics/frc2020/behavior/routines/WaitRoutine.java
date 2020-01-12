package com.palyrobotics.frc2020.behavior.routines;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import edu.wpi.first.wpilibj.Timer;

import java.util.HashSet;
import java.util.Set;

public class WaitRoutine extends Routine {

    private double mTimeout;
    private final Timer mTimer = new Timer();

    /**
     * Routine that waits the specified amount of time.
     * Does not require any subsystems.
     *
     * @param waitTime Time to wait in seconds
     */
    public WaitRoutine(double waitTime) {
        mTimeout = waitTime;
    }

    @Override
    public void start() {
        mTimer.reset();
        mTimer.start();
    }

    @Override
    public boolean checkFinished() {
        return mTimer.get() > mTimeout;
    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return new HashSet<>();
    }
}
