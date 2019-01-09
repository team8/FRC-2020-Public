package com.palyrobotics.frc2019.behavior.routines.Shovel;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Shovel;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class ShovelWheelRoutine extends Routine {

    private Shovel.WheelState wantedWheelState;

    private double mTimeout;

    private long mStartTime;

    public ShovelWheelRoutine(Shovel.WheelState wantedWheelState, double timeout) {
        this.wantedWheelState = wantedWheelState;
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedShovelWheelState = wantedWheelState;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedShovelWheelState = Shovel.WheelState.IDLE;
        return commands;
    }

    @Override
    public boolean finished() {
        return System.currentTimeMillis() - mStartTime > mTimeout * 1000;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { shovel };
    }

    @Override
    public String getName() {
        return "ShovelWheelRoutine";
    }
}
