package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import edu.wpi.first.wpilibj.Timer;

import java.util.Set;

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
        mTimer.reset();
        mTimer.start();
    }

    @Override
    protected void update(Commands commands) {
        commands.setDriveSignal(mOutput);
    }

    @Override
    public boolean checkFinished() {
        return mTimer.get() > mDurationSeconds;
    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return Set.of(mDrive);
    }
}
