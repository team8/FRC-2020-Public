package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.util.SparkDriveSignal;

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
    public ArrayList<Routine> wantedRoutines = new ArrayList<>();
    // Store WantedStates for each subsystem state machine
    public Drive.DriveState wantedDriveState = Drive.DriveState.NEUTRAL;
    public Spinner.SpinnerState wantedSpinnerState = Spinner.SpinnerState.IDLE;
    public Intake.IntakeState wantedIntakeState = Intake.IntakeState.IDLE;
    public Shooter.ShooterState wantedShooterState = Shooter.ShooterState.IDLE;
    public Shooter.HoodState wantedHoodState = Shooter.HoodState.LOW;
    public double driveThrottle, driveWheel;
    public boolean isQuickTurn, isBraking;
    // All robot set points
    public SetPoints robotSetPoints = new SetPoints();
    // Allows you to cancel all running routines
    public boolean cancelCurrentRoutines;

    public static Commands getInstance() {
        return sInstance;
    }

    public static Commands reset() {
        sInstance = new Commands();
        return sInstance;
    }

    public void addWantedRoutine(Routine wantedRoutine) {
        for (Routine routine : wantedRoutines) {
            if (routine.getClass().equals(wantedRoutine.getClass())) {
//                Logger.getInstance().logRobotThread(Level.WARNING, "tried to add duplicate routine", routine.getName());
                return;
            }
        }
        wantedRoutines.add(wantedRoutine);
    }

    public void copyTo(Commands other) {
        other.wantedDriveState = this.wantedDriveState;
        other.wantedIntakeState = this.wantedIntakeState;
        other.cancelCurrentRoutines = this.cancelCurrentRoutines;

        other.wantedRoutines.addAll(this.wantedRoutines);

        // Copy optionals that are present
        other.robotSetPoints.drivePowerSetPoint = robotSetPoints.drivePowerSetPoint;
        other.robotSetPoints.elevatorPositionSetPoint = robotSetPoints.elevatorPositionSetPoint;
        other.robotSetPoints.intakePositionSetPoint = robotSetPoints.intakePositionSetPoint;
        other.robotSetPoints.pusherPositionSetPoint = robotSetPoints.pusherPositionSetPoint;
        other.robotSetPoints.shooterVelocitySetPoint = robotSetPoints.shooterVelocitySetPoint;

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

    /**
     * Stores numeric set points
     *
     * @author Nihar
     */
    public static class SetPoints {
        public SparkDriveSignal drivePowerSetPoint;
        public Double
                elevatorPositionSetPoint,
                pusherPositionSetPoint,
                intakePositionSetPoint,
                shooterVelocitySetPoint;

    }
}