package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.ShooterVisionRoutine;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

public class StartCenterShootThreeRendezvousFiveShootFive extends AutoBase {

    @Override
    public RoutineBase getRoutine() {
        var setInitialOdometry = new DriveSetOdometryRoutine(126, 200, 180);
        var initialShoot = new ParallelRoutine(
                new ShooterVisionRoutine(3.0),
                new SequentialRoutine(
                        new TimedRoutine(0.8),
                        new IndexerFeedAllRoutine(2.2, false, true)));
        var getBalls = new SequentialRoutine(
                new DrivePathRoutine(newWaypoint(211, 171, 19))
                        .setMovement(1.6 , 1.4),
                new IntakeBallRoutine(1),
                new DrivePathRoutine(newWaypoint(294, 210, -25))
                        .setMovement(1.6, 1.4),
                new DrivePathRoutine(newWaypoint(298, 182, 85))
                        .setMovement(1.6, 1.4),
                new DrivePathRoutine(newWaypoint(216, 147, 157))
                        .setMovement(1.6, 1.4));
        var shoot = new SequentialRoutine(
                new ShooterVisionRoutine(5),
                new SequentialRoutine(
                        new TimedRoutine(0.2),
                        new IndexerFeedAllRoutine(3.8, false, true)));
        return new SequentialRoutine(setInitialOdometry, initialShoot, getBalls, shoot);
    }
}
