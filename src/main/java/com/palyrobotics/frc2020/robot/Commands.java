package com.palyrobotics.frc2020.robot;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

import java.util.ArrayList;

/**
 * Commands represent what we want the robot to be doing.
 *
 * @author Nihar
 */
public class Commands {

    private static Commands sInstance = new Commands();

    public static Commands getInstance() {
        return sInstance;
    }

    public static Commands resetInstance() {
        sInstance = new Commands();
        return sInstance;
    }

    /* Routines */
    public ArrayList<Routine> routinesWanted = new ArrayList<>();
    public boolean shouldClearCurrentRoutines;

    /* Spinner Commands */
    public Spinner.SpinnerState spinnerWantedState = Spinner.SpinnerState.IDLE;

    /* Intake Commands */
    public Intake.IntakeState intakeWantedState = Intake.IntakeState.IDLE;

    /* Drive Commands */
    private Drive.DriveState driveWantedState = Drive.DriveState.NEUTRAL;
    // Teleop
    private double driveThrottle, driveWheel;
    private boolean driveWantsQuickTurn, driveWantsBrake;
    // Signal
    private SparkDriveSignal driveWantedSignal;
    // Path Following
    private Trajectory driveWantedTrajectory;

    public void addWantedRoutines(Routine... wantedRoutines) {
        for (Routine wantedRoutine : wantedRoutines) {
            addWantedRoutine(wantedRoutine);
        }
    }

    public void addWantedRoutine(Routine wantedRoutine) {
        routinesWanted.add(wantedRoutine);
    }

    public Drive.DriveState getDriveState() {
        return driveWantedState;
    }

    public boolean getDriveWantsQuickTurn() {
        return driveWantsQuickTurn;
    }

    public double getDriveThrottle() {
        return driveThrottle;
    }

    public double getDriveWheel() {
        return driveWheel;
    }

    public boolean getWantsBreak() {
        return driveWantsBrake;
    }

    public Trajectory getDriveTrajectory() {
        return driveWantedTrajectory;
    }

    public SparkDriveSignal getWantedDriveSignal() {
        return driveWantedSignal;
    }

    public void setDriveSignal(SparkDriveSignal signal) {
        driveWantedState = Drive.DriveState.SIGNAL;
        driveWantedSignal = signal;
    }

    public void setDriveFollowPath(Trajectory trajectory) {
        driveWantedState = Drive.DriveState.FOLLOW_PATH;
        driveWantedTrajectory = trajectory;
    }

    public void setDriveVisionAlign() {
        driveWantedState = Drive.DriveState.VISION_ALIGN;
    }

    public void setDriveTeleop() {
        setDriveTeleop(0.0, 0.0, false, false);
    }

    public void setDriveTeleop(
            double driveThrottle, double driveWheel,
            boolean driveWantsQuickTurn, boolean driveWantsBrake) {
        driveWantedState = Drive.DriveState.TELEOP;
        this.driveThrottle = driveThrottle;
        this.driveWheel = driveWheel;
        this.driveWantsQuickTurn = driveWantsQuickTurn;
        this.driveWantsBrake = driveWantsBrake;
    }

    public void copyTo(Commands other) {
        other.driveWantedState = this.driveWantedState;
        other.intakeWantedState = this.intakeWantedState;
        other.shouldClearCurrentRoutines = this.shouldClearCurrentRoutines;
        other.routinesWanted.addAll(this.routinesWanted);
    }

    @Override
    public String toString() {
        StringBuilder log = new StringBuilder();
        log.append("Wanted routines: ");
        for (Routine r : routinesWanted) {
            log.append(r.getName()).append(" ");
        }
        return log.append("\n").toString();
    }

    public void setDriveNeutral() {
        driveWantedState = Drive.DriveState.NEUTRAL;
    }
}