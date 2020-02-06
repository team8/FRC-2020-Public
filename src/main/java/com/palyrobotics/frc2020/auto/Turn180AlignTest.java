package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawAlignRoutine;

public class Turn180AlignTest extends AutoBase{

    @Override
    public RoutineBase getRoutine() {
        return new DriveYawAlignRoutine(180);
    }
}
