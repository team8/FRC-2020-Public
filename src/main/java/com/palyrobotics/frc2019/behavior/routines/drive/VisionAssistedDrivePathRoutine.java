package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbie on 3/18/19.
 */
public class VisionAssistedDrivePathRoutine extends Routine {
    private Path mPath;
    private ArrayList<Path.Waypoint> mPathList;
    private double mLookAhead;
    private boolean mInverted, mRelative;
    private String mEnableVisionMarker;
    private boolean startedRoutine;

    public VisionAssistedDrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, boolean relative, String enableVisionMarker) {
        mPath = new Path(pathList);
        mPathList = pathList;
        mInverted = inverted;
        mLookAhead = DrivetrainConstants.kPathFollowingLookahead;
        mRelative = relative;
        mEnableVisionMarker = enableVisionMarker;
    }

    @Override
    public void start() {
        if (mRelative) {
            ArrayList<Path.Waypoint> absoluteList = new ArrayList<>();
            for (Path.Waypoint point : mPathList) {
                if (point.isRelative) {
                    if (point.marker != null) {
                        absoluteList.add(new Path.Waypoint(mRobotState.getLatestFieldToVehicle().getValue().getTranslation().translateBy(point.position), point.speed, point.marker, false));
                    } else {
                        absoluteList.add(new Path.Waypoint(mRobotState.getLatestFieldToVehicle().getValue().getTranslation().translateBy(point.position), point.speed, false));
                    }
                } else {
                    absoluteList.add(point);
                }
            }
            mPath = new Path(absoluteList);
        }
        mDrive.setTrajectoryController(mPath, mLookAhead, mInverted, DrivetrainConstants.kPathFollowingTolerance);
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
        mPath = getPath();
        if (mPath.getMarkersCrossed().contains(mEnableVisionMarker) && !startedRoutine) {
            startedRoutine = true;
            Drive.getInstance().setVisionClosedDriveController();
        }
        if (startedRoutine) {
            Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.VISION);
            Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON); // Limelight LED on
            commands.wantedDriveState = Drive.DriveState.CLOSED_VISION_ASSIST;
        }

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        mDrive.setNeutral();
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
        return commands;
    }

    public Path getPath() {
        return mPath;
    }

    @Override
    public boolean isFinished() {
        return mDrive.controllerOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }

    @Override
    public String getName() {
        return "Vision Drive Path Routine";
    }

    @Override
    public String toString() {
        var enumeratedPath = new StringBuilder();
        List<Path.Waypoint> path = mPath.getWayPoints();
        enumeratedPath.append("0,0,0\n");
        for (Path.Waypoint waypoint : path) {
            enumeratedPath.append(waypoint.position.getX()).append(",").append(waypoint.position.getY()).append(",").append(waypoint.speed).append("\n");
        }
        return enumeratedPath.toString();
    }
}
