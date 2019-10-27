package com.palyrobotics.frc2019.behavior;

import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.Path;

/**
 * Created by Justin on 3/22/18.
 */
public class WaypointTriggerRoutine extends Routine {
    private Routine mRoutine;
    private DrivePathRoutine mDrivePathRoutine;
    private Path mPath;
    private String mMarker;
    private boolean startedRoutine = false;

    public WaypointTriggerRoutine(Routine routine, DrivePathRoutine path, String marker) {
        mRoutine = routine;
        mDrivePathRoutine = path;
        mPath = path.getPath();
        mMarker = marker;
    }

    @Override
    public void start() {

    }

    @Override
    public Commands update(Commands commands) {
        mPath = mDrivePathRoutine.getPath();
        if (mPath.getMarkersCrossed().contains(mMarker) && !startedRoutine) {
            mRoutine.start();
            startedRoutine = true;
        }
        if (startedRoutine) {
            mRoutine.update(commands);
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return mRoutine.cancel(commands);
    }

    @Override
    public boolean isFinished() {
        return startedRoutine && mRoutine.isFinished();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return mRoutine.getRequiredSubsystems();
    }

    @Override
    public String getName() {
        return String.format("Waypoint trigger routine of (%s %s)", mRoutine.getName(), mDrivePathRoutine.getName());
    }
}
