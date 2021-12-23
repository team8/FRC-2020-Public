package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import java.util.function.Predicate;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveParallelPathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;
import com.palyrobotics.frc2020.robot.OperatorInterface;
import com.palyrobotics.frc2020.subsystems.Shooter;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.util.Units;

//4, 3, left, move 3, turn right

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

public class StartLeftCornerMoveTurnDrive extends AutoBase {
    @Override
    public RoutineBase getRoutine() {
        var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 180);
        var sequentialRoutine = new SequentialRoutine(
            new DrivePathRoutine(
                    newWaypoint(4,0,0),
                    newWaypoint(7,0,0))
                    .setMovement(3,3)
            );

            new DriveAlignYawAssistedRoutine(90, OperatorInterface.kOnesTimesZoomAlignButton);

            new DrivePathRoutine(
                    newWaypoint(3,0,0)
            );

        return new SequentialRoutine(setInitialOdometry, sequentialRoutine);
    }
}
