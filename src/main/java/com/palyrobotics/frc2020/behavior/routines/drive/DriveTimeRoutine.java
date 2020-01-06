package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import edu.wpi.first.wpilibj.Timer;

public class DriveTimeRoutine extends Routine {

    private final Timer mTimer = new Timer();
    private final double mDurationSeconds;
    private final SparkDriveSignal mOutput;

    public DriveTimeRoutine(double durationSeconds, SparkDriveSignal output) {
        mDurationSeconds = durationSeconds;
        mOutput = output;
    }

    @Override
    public void start() {
        mDrive.resetController();
        mTimer.reset();
        mTimer.start();
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.OPEN_LOOP;
        commands.robotSetPoints.drivePowerSetPoint = mOutput;
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
        mDrive.resetController();
        mDrive.setNeutral();
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mTimer.hasPeriodPassed(mDurationSeconds);
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }
}
