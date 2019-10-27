package com.palyrobotics.frc2019.behavior.routines;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import edu.wpi.first.wpilibj.Timer;

public class TimeoutRoutine extends Routine {
    private double mTimeout, mStartTime;

    /**
     * Routine that waits the specified amount of time <br />
     * Does not require any subsystems
     *
     * @param waitTime Time to wait in seconds
     */
    public TimeoutRoutine(double waitTime) {
        mTimeout = waitTime;
    }

    @Override
    public void start() {
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public Commands update(Commands commands) {
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() >= mStartTime + mTimeout;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{};
    }

    @Override
    public String getName() {
        return "Timeout Routine";
    }

}
