package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.Routine;

public class ParallelDrivePathRoutine extends ParallelRoutine { // TODO implement class

    private final Routine m_Routine;
    private final DrivePathRoutine m_DrivePathRoutine;
    private final double m_PercentageComplete;

    public ParallelDrivePathRoutine(Routine routine, DrivePathRoutine drivePathRoutine, double percentageComplete) {
        m_Routine = routine;
        m_DrivePathRoutine = drivePathRoutine;
        m_PercentageComplete = percentageComplete;
    }
}
