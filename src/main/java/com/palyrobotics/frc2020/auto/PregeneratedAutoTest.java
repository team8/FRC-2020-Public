package com.palyrobotics.frc2020.auto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathPremadeRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

public class PregeneratedAutoTest extends AutoBase{


    @Override
    public RoutineBase getRoutine() throws JsonProcessingException {
        var temp = new DrivePathPremadeRoutine("src/main/deploy/built-trajectories/output/2BallAuto.wpilib.json");
        return temp;
    }
}
