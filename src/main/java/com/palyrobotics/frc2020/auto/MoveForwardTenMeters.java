package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

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

/**
 * Start by facing and aligning the center of the intake to the middle of the two balls of the
 * opponent's trench. Pull the robot back until just the back bumper covers the initiation line. The
 * left ball should be centered in the intake from this configuration.
 */
@SuppressWarnings("Duplicates")
public class TrenchStealTwoShootFive extends AutoBase {

    @Override
    public RoutineBase getRoutine() {

        var move = new ParallelRaceRoutine(
                new DrivePathRoutine(newWaypoint(92, 0, 0))
                        .setMovement(1.5, 2.4),
                );

        return move;
    }
}
