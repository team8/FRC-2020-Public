package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

/**
 * Start with enough space to drive forward 10 meters
 */

public class TenMeterForward extends AutoBase{
    @Override
    public RoutineBase getRoutine() {
        var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);

        var goTenMeters = new DrivePathRoutine(newWaypoint(394, 0, 0))
                .setMovement(1.5, 2.4);

        return new SequentialRoutine(setInitialOdometry, goTenMeters);
    }
}
