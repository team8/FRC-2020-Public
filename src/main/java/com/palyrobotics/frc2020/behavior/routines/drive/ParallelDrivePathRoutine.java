package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.Routine;

public class ParallelDrivePathRoutine extends ParallelRoutine { // TODO implement class

    private final Routine mRoutine;
    private final DrivePathRoutine mDrivePathRoutine;
    private final double mPercentageComplete;

    public ParallelDrivePathRoutine(Routine routine, DrivePathRoutine drivePathRoutine, double percentageComplete) {
        mRoutine = routine;
        mDrivePathRoutine = drivePathRoutine;
        mPercentageComplete = percentageComplete;
    }
}
