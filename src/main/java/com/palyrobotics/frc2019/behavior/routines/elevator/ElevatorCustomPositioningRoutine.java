package com.palyrobotics.frc2019.behavior.routines.elevator;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.Path;

import java.util.Optional;

public class ElevatorCustomPositioningRoutine extends Routine {

    private double mPosition;
    private double mTimeout;
    private long mStartTime = -1;

    private Optional<Path> mPath = Optional.empty();
    private Optional<String> mRoutineStartWayPoint = Optional.empty();

    public ElevatorCustomPositioningRoutine(double position, double timeout) {
        this.mPosition = position;
        this.mTimeout = timeout;
    }

    public ElevatorCustomPositioningRoutine(double position, double timeout, Path path, String routineStartWaypoint) {
        this.mPosition = position;
        this.mTimeout = timeout;
        this.mPath = Optional.of(path);
        this.mRoutineStartWayPoint = Optional.of(routineStartWaypoint);
    }

    @Override
    public void start() {
        if(!mPath.isPresent()) {
            mStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public Commands update(Commands commands) {
        if(!mPath.isPresent() || (mRoutineStartWayPoint.isPresent() && mPath.get().getMarkersCrossed().contains(mRoutineStartWayPoint.get()))) {
            if(mStartTime == -1) mStartTime = System.currentTimeMillis();
            commands.wantedGearboxState = Elevator.GearboxState.ELEVATOR;
            commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
            commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(mPosition);
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedGearboxState = Elevator.GearboxState.ELEVATOR;
        commands.wantedElevatorState = Elevator.ElevatorState.HOLD;
        return commands;
    }

    @Override
    public boolean finished() {
        if(mStartTime != -1) {
            if (System.currentTimeMillis() - mStartTime > mTimeout * 1000) {
                return true;
            }
        }

//        if(elevator.getElevatorWantedPosition().isPresent()) {
//            if(elevator.getElevatorWantedPosition().get() == ElevatorConstants.kBottomPositionInches) {
//                return true;
//            }
//        }

        return elevator.elevatorOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { elevator };
    }

    @Override
    public String getName() {
        return "ElevatorCustomPositioningRoutine";
    }
}
