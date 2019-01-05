package com.palyrobotics.frc2018.behavior;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Subsystem;
import com.palyrobotics.frc2018.util.trajectory.Path;

/**
 * Created by Justin on 3/22/18.
 */
public class WaypointTriggerRoutine extends Routine{
    private Routine mRoutine;
    private DrivePathRoutine mDrivePathRoutine;
    private Path mPath;
    private String mMarker;
    private boolean startedRoutine = false;

    public WaypointTriggerRoutine(Routine routine, DrivePathRoutine path, String marker) {
        this.mRoutine = routine;
        this.mDrivePathRoutine = path;
        this.mPath = path.getPath();
        this.mMarker = marker;
    }

    @Override
    public void start() {

    }

    @Override
    public Commands update(Commands commands) {
        this.mPath = mDrivePathRoutine.getPath();
        if(mPath.getMarkersCrossed().contains(mMarker) && !startedRoutine) {
            mRoutine.start();
            startedRoutine = true;
        }
        if(startedRoutine) {
            mRoutine.update(commands);
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return mRoutine.cancel(commands);
    }

    @Override
    public boolean finished() {
        return startedRoutine && mRoutine.finished();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return mRoutine.getRequiredSubsystems();
    }

    @Override
    public String getName() {
        String name = "WaypointTriggerRoutine of (";
        name += mRoutine.getName();
        name += " ";
        name += mDrivePathRoutine.getName();
        return name + ")";
    }
}
