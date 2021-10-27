package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerTimeRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeBallRoutine;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

public class TrenchStealThreeShootThree extends AutoBase {

    @Override
    public RoutineBase getRoutine() {
        var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);

        var getTrenchBalls = new ParallelRaceRoutine(
                new SequentialRoutine(
                        new DrivePathRoutine(newWaypoint(88, 0, 0)).setMovement(1.5, 2.5),
                        new DriveYawRoutine(12.0)),
                new IndexerTimeRoutine(Double.POSITIVE_INFINITY),
                new IntakeBallRoutine(Double.POSITIVE_INFINITY, 1.0));
        return new SequentialRoutine(setInitialOdometry, getTrenchBalls);
    }
}
