package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.SparkDriveSignal;

import java.util.ArrayList;

/**
 * Commands represent the desired set points and subsystem states for the robot. <br />
 * Store Requests (enum) for each subsystem and set points {@link SetPoints} <br />
 * Variables are public and have default values to prevent NullPointerExceptions
 *
 * @author Nihar
 */
public class Commands {

    private static Commands sInstance = new Commands();

    public static Commands getInstance() {
        return sInstance;
    }

    public ArrayList<Routine> wantedRoutines = new ArrayList<>();

    // Store WantedStates for each subsystem state machine
    public Drive.DriveState wantedDriveState = Drive.DriveState.NEUTRAL;

    public Shooter.ShooterState wantedShooterState = Shooter.ShooterState.IDLE;

    public Pusher.PusherState wantedPusherInOutState = Pusher.PusherState.START;

    public Shovel.WheelState wantedShovelWheelState = Shovel.WheelState.IDLE;
    public Shovel.UpDownState wantedShovelUpDownState = Shovel.UpDownState.UP;

    public Fingers.FingersState wantedFingersOpenCloseState = Fingers.FingersState.OPEN;
    public Fingers.PushingState wantedFingersExpelState = Fingers.PushingState.CLOSED;
    public boolean blockFingers;

    public Intake.IntakeMacroState wantedIntakeState = Intake.IntakeMacroState.HOLDING;

    public boolean disableIntakeScaling = true;

    public boolean customShooterSpeed, customIntakeSpeed;

    public Elevator.ElevatorState wantedElevatorState = Elevator.ElevatorState.IDLE;
    public double customElevatorPercentOutput;

    public boolean customShovelSpeed;
    public boolean autoPlacerOutput;

    public boolean intakeHFX, intakeHasHatch;
    public boolean hasCargo, hasPusherCargo;

    public boolean shooterSpinning;

    public void addWantedRoutine(Routine wantedRoutine) {
        for (Routine routine : wantedRoutines) {
            if (routine.getClass().equals(wantedRoutine.getClass())) {
//                Logger.getInstance().logRobotThread(Level.WARNING, "tried to add duplicate routine", routine.getName());
                return;
            }
        }
        wantedRoutines.add(wantedRoutine);
    }

    public static Commands reset() {
        sInstance = new Commands();
        return sInstance;
    }

    /**
     * Stores numeric set points
     *
     * @author Nihar
     */
    public static class SetPoints {
        public SparkDriveSignal drivePowerSetpoint;
        public Double
                elevatorPositionSetpoint,
                climberPositionSetpoint,
                pusherPositionSetpoint,
                intakePositionSetpoint,
                climbRateSetpoint;

        /**
         * Resets all the set points
         */
        public void reset() {
            drivePowerSetpoint = null;
            elevatorPositionSetpoint = null;
            climberPositionSetpoint = null;
            pusherPositionSetpoint = null;
            intakePositionSetpoint = null;
            climbRateSetpoint = null;
        }
    }

    // All robot set points
    public SetPoints robotSetPoints = new SetPoints();

    // Allows you to cancel all running routines
    public boolean cancelCurrentRoutines;

    /**
     * @return a copy of these commands
     */
    public Commands copy() {
        Commands copy = new Commands();
        copy.wantedDriveState = this.wantedDriveState;
        copy.wantedShooterState = this.wantedShooterState;
        copy.wantedElevatorState = this.wantedElevatorState;
        copy.cancelCurrentRoutines = this.cancelCurrentRoutines;
        copy.wantedPusherInOutState = this.wantedPusherInOutState;
        copy.wantedShovelWheelState = this.wantedShovelWheelState;
        copy.wantedShovelUpDownState = this.wantedShovelUpDownState;
        copy.wantedFingersOpenCloseState = this.wantedFingersOpenCloseState;
        copy.wantedFingersExpelState = this.wantedFingersExpelState;
        copy.customShooterSpeed = this.customShooterSpeed;
        copy.customIntakeSpeed = this.customIntakeSpeed;
        copy.customShovelSpeed = this.customShovelSpeed;
        copy.autoPlacerOutput = this.autoPlacerOutput;
        copy.wantedIntakeState = this.wantedIntakeState;
        copy.disableIntakeScaling = this.disableIntakeScaling;
        copy.hasPusherCargo = this.hasPusherCargo;
        copy.shooterSpinning = this.shooterSpinning;

        copy.wantedRoutines.addAll(this.wantedRoutines);

        // Copy robot set points
        copy.robotSetPoints = new SetPoints();
        // Copy optionals that are present
        copy.robotSetPoints.drivePowerSetpoint = robotSetPoints.drivePowerSetpoint;
        copy.robotSetPoints.elevatorPositionSetpoint = robotSetPoints.elevatorPositionSetpoint;
        copy.robotSetPoints.climberPositionSetpoint = robotSetPoints.climberPositionSetpoint;
        copy.robotSetPoints.intakePositionSetpoint = robotSetPoints.intakePositionSetpoint;
        copy.robotSetPoints.pusherPositionSetpoint = robotSetPoints.pusherPositionSetpoint;
        copy.robotSetPoints.climbRateSetpoint = robotSetPoints.climbRateSetpoint;
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder log = new StringBuilder();
        log.append("Wanted routines: ");
        for (Routine r : this.wantedRoutines) {
            log.append(r.getName()).append(" ");
        }
        return log.append("\n").toString();
    }
}