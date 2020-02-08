package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeStowRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;


public abstract class RendezvousRoutine extends AutoBase {
    public SequentialRoutine CenterRendezvousThree(){
    return new SequentialRoutine(
            new DriveSetOdometryRoutine(0, 0, 180),
            new DriveYawRoutine(-45),
            getBallsRoutine,
            new DrivePathRoutine(newWaypoint(80, -70, 0)),
            //get ball 1
            new DrivePathRoutine(newWaypoint(30, -70, 0)).reverse(),
            //back up
            new DrivePathRoutine(newWaypoint(85, -90, 0)),
            // get ball 2
            new IntakeStowRoutine(),
            new DriveAlignYawAssistedRoutine(180, kOneTimesZoomPipelineId),
            shootBallsRoutine);
    }
}
