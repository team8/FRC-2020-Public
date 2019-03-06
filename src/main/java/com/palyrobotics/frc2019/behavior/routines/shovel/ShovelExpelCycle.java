package com.palyrobotics.frc2019.behavior.routines.shovel;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Shovel;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class ShovelExpelCycle extends Routine {

    private Fingers.FingersState wantedFingersOpenCloseState;

    private boolean alreadyRan;
    private double timeout;
    private double startTime;

    private final double timeToShoot = 50;

    public ShovelExpelCycle(double timeout) {
        this.timeout = timeout * 1000;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        alreadyRan = false;
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedShovelUpDownState = Shovel.UpDownState.DOWN;
        commands.wantedShovelWheelState = Shovel.WheelState.EXPELLING;

        if(System.currentTimeMillis() > this.timeout + startTime) {
            commands.wantedShovelUpDownState = Shovel.UpDownState.UP;
            commands.wantedShovelWheelState = Shovel.WheelState.IDLE;
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