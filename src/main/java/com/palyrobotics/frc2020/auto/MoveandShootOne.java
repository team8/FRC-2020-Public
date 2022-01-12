package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.Shooter;

public class MoveandShootOne extends AutoBase {
    @Override

    public RoutineBase getRoutine() {
        var setInitialOdometry = new DriveSetOdometryRoutine(0,0,0);

        var intakeLowerRoutine = new IntakeLowerRoutine();

        var getBalls = new ParallelRaceRoutine(
                new DrivePathRoutine(newWaypoint (30, 0, 0))
                        .setMovement(1.5, 1.5),

                new SequentialRoutine(
                        new IndexerTimeRoutine(Double.POSITIVE_INFINITY),
                        new IntakeBallRoutine(Double.POSITIVE_INFINITY, 1.0))

        );

        /*var shootBalls = new SequentialRoutine(
            new DriveAlignRoutine(0),
            new ShooterVisionRoutine(3),
            new TimedRoutine(0.3)
        );*/

        var shoot = new SequentialRoutine(
                new DriveYawRoutine(200),
                new ShooterVisionRoutine(5),
                new SequentialRoutine(
                        new TimedRoutine(0.2),
                        new IndexerFeedAllRoutine(3.8, false, true)));

        return new SequentialRoutine(setInitialOdometry, intakeLowerRoutine, getBalls, shoot);
    }
}
