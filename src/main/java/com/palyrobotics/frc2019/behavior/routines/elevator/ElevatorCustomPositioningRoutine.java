package com.palyrobotics.frc2019.behavior.routines.elevator;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.Path;

import java.util.Optional;

public class ElevatorCustomPositioningRoutine extends Routine {

    private double mPosition;
    private double mTimeout;
    private long mStartTime = -1;
    private boolean hasSetAllVars = false;

    private Optional<Path> mPath = Optional.empty();
    private Optional<String> mRoutineStartWayPoint = Optional.empty();

    public ElevatorCustomPositioningRoutine(double position, double timeout) {
        this.mPosition = position;
        this.mTimeout = timeout;
    }

    public ElevatorCustomPositioningRoutine(double position, double timeout, Path path, String routineStartWayPoint) {
        this.mPosition = position;
        this.mTimeout = timeout;
        this.mPath = Optional.of(path);
        this.mRoutineStartWayPoint = Optional.of(routineStartWayPoint);
    }

    @Override
    public void start() {
        if (mPath.isEmpty()) {
            mStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public Commands update(Commands commands) {
        if (mPath.isEmpty() || (mRoutineStartWayPoint.isPresent() && mPath.get().getMarkersCrossed().contains(mRoutineStartWayPoint.get()))) {
            if (mStartTime == -1) mStartTime = System.currentTimeMillis();
            hasSetAllVars = true;
            commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
            commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(mPosition);
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.CUSTOM_POSITIONING;
//        System.out.println("cancelling all fucktacklind");
        return commands;
    }

    @Override
    public boolean finished() {
        if (mStartTime != -1) {
            if (System.currentTimeMillis() - mStartTime > mTimeout * 1000) {
//                System.out.println("ECPR timing out");
                return true;
            }
        }

//        if(elevator.getElevatorWantedPosition().isPresent(E)) {
//            if(elevator.getElevatorWantedPosition().get() == ElevatorConstants.kBottomPositionInches) {
//                return true;
//            }
//        }

        System.out.println("Cancelling this");

//        return hasSetAllVars && elevator.elevatorOnTarget();
        return false;
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
