package com.palyrobotics.frc2020.behavior.routines.climber;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Climber;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class ClimberCustomPositioningRoutine extends Routine {

    private double mPosition;
    public ClimberCustomPositioningRoutine(double position) {
        mPosition = position;
    }

    @Override
    public void start() {
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedClimberState = Climber.ClimberState.CUSTOM_POSITION;
        commands.robotSetPoints.climberPositionSetPoint = mPosition;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedClimberState = Climber.ClimberState.IDLE;
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mClimber.climberOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { mClimber };
    }
}
