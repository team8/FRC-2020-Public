package com.palyrobotics.frc2019.behavior.routines.shooter;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.subsystems.Subsystem;

/**
 * @author Jason, Alan
 */
public class ShooterExpelRoutine extends Routine {

    private Shooter.ShooterState wantedShooterState;

    //How long the wheels spin for (seconds)
    private double mTimeout;

    private long mStartTime;

    /**
     *
     * @param wantedShooterState the desired state
     * @param timeout how long (seconds) to run for
     */
    public ShooterExpelRoutine(Shooter.ShooterState wantedShooterState, double timeout) {
        this.wantedShooterState = wantedShooterState;
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public Commands update(Commands commands) {

        commands.wantedShooterState = wantedShooterState;
        commands.customShooterSpeed = false;

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedShooterState = Shooter.ShooterState.IDLE;
        return commands;
    }

    @Override
    public boolean finished() {
        return System.currentTimeMillis() - mStartTime > mTimeout * 1000;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { shooter };
    }

    @Override
    public String getName() {
        return "ShooterExpelRoutine";
    }
}
