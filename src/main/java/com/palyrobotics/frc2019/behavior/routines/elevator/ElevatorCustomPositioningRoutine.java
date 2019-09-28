package com.palyrobotics.frc2019.behavior.routines.elevator;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.Path;

public class ElevatorCustomPositioningRoutine extends Routine {

    private double mPosition;
    private double mTimeout;
    private Long mStartTime;
    private boolean hasSetAllVars = false;

    private Path mPath;
    private String mRoutineStartWayPoint;

    public ElevatorCustomPositioningRoutine(double position, double timeout) {
        mPosition = position;
        mTimeout = timeout;
    }

    public ElevatorCustomPositioningRoutine(double position, double timeout, Path path, String routineStartWayPoint) {
        this.mPosition = position;
        this.mTimeout = timeout;
        this.mPath = path;
        this.mRoutineStartWayPoint = routineStartWayPoint;
    }

    @Override
    public void start() {
        if (mPath == null) {
            mStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public Commands update(Commands commands) {
        if (mPath == null || (mRoutineStartWayPoint != null && mPath.getMarkersCrossed().contains(mRoutineStartWayPoint))) {
            if (mStartTime == null) mStartTime = System.currentTimeMillis();
            hasSetAllVars = true;
            commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
            commands.robotSetPoints.elevatorPositionSetpoint = mPosition;
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
        return commands;
    }

    @Override
    public boolean finished() {
        if (mStartTime != null) {
            if (System.currentTimeMillis() - mStartTime > mTimeout * 1000) {
                return true;
            }
        }
        return hasSetAllVars && elevator.elevatorOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{elevator};
    }

    @Override
    public String getName() {
        return "ElevatorCustomPositioningRoutine";
    }
}
