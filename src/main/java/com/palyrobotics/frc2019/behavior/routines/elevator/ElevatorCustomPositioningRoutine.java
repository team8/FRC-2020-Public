package com.palyrobotics.frc2019.behavior.routines.elevator;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import edu.wpi.first.wpilibj.Timer;

public class ElevatorCustomPositioningRoutine extends Routine {

    private final Timer mTimeoutTimer = new Timer();
    private double mPosition, mTimeout;

    public ElevatorCustomPositioningRoutine(double position, double timeout) {
        mPosition = position;
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mTimeoutTimer.reset();
        mTimeoutTimer.start();
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
        commands.robotSetPoints.elevatorPositionSetPoint = mPosition;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mTimeoutTimer.hasPeriodPassed(mTimeout) || mElevator.elevatorOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mElevator};
    }
}
