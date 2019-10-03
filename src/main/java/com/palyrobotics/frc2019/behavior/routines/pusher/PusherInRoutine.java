package com.palyrobotics.frc2019.behavior.routines.pusher;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.PusherConfig;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.configv2.Configs;

public class PusherInRoutine extends Routine {

    @Override
    public void start() {

    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedPusherInOutState = Pusher.PusherState.IN;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean finished() {
        return RobotState.getInstance().pusherPosition < Configs.get(PusherConfig.class).acceptablePositionError;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { pusher };
    }

    @Override
    public String getName() {
        return "PusherInRoutine";
    }
}
