package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
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
    private ArrayList<Path.Waypoint> pathList;
    private double mLookAhead;
    private double mStartSpeed;
    private boolean mInverted;
    private double mTolerance = DrivetrainConstants.kPathFollowingTolerance;
    private boolean mRelative;
    private String enableVisionMarker;
    private boolean startedRoutine;

    public VisionAssistedDrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, boolean relative, String enableVisionMarker) {
        this.mPath = new Path(pathList);
        this.pathList = pathList;
        this.mInverted = inverted;
        this.mLookAhead = DrivetrainConstants.kPathFollowingLookahead;
        this.mStartSpeed = 0.0;
        this.mRelative = relative;
        this.enableVisionMarker = enableVisionMarker;
    }

    @Override
    public void start() {

        if(mRelative) {
            ArrayList<Path.Waypoint> absoluteList = new ArrayList<>();
            for (Path.Waypoint point : pathList) {
                if (point.isRelative) {
                    if (point.marker.isPresent()) {
                        absoluteList.add(new Path.Waypoint(robotState.getLatestFieldToVehicle().getValue().getTranslation().translateBy(point.position), point.speed, point.marker.get(), false));
                    } else {
                        absoluteList.add(new Path.Waypoint(robotState.getLatestFieldToVehicle().getValue().getTranslation().translateBy(point.position), point.speed, false));
                    }
                } else {
                    absoluteList.add(point);
                }
            }

            int counter = 0;

            for (Path.Waypoint point : absoluteList) {
                counter++;
            }

            mPath = new Path(absoluteList);
        }
//        Logger.getInstance().logSubsystemThread(Level.INFO, "Starting Drive Path Routine");

        drive.setTrajectoryController(mPath, mLookAhead, mInverted, mTolerance);
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
        this.mPath = this.getPath();
        System.out.println(mPath.getMarkersCrossed().toString());
        if(mPath.getMarkersCrossed().contains(enableVisionMarker) && !startedRoutine) {
            startedRoutine = true;
            Drive.getInstance().setVisionClosedDriveController();
        }
        if(startedRoutine) {
//            System.out.println("Vision Assist Mode");
            Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.VISION);
            Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON); // Limelight LED on
            commands.wantedDriveState = Drive.DriveState.CLOSED_VISION_ASSIST;
        }

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
//        Logger.getInstance().logSubsystemThread(Level.INFO, "Drive Path Routine finished");
        drive.setNeutral();
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
        return commands;
    }

    public Path getPath() {
        return mPath;
    }

    @Override
    public boolean finished() {
        return drive.controllerOnTarget();
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { drive };
    }

    @Override
    public String getName() {
        return "Vision DrivePathRoutine";
    }

    @Override
    public String toString() {
        final int offsetX = 0;
        final int offsetY = 0;
        String enumeratedPath = "";
        List<Path.Waypoint> path = mPath.getWayPoints();
        enumeratedPath += "0,0,0\n";
        for (int i = 0; i < path.size(); i++) {
            enumeratedPath += (path.get(i).position.getX() +offsetX)  + "," + (path.get(i).position.getY() + offsetY) + "," + path.get(i).speed + "\n";
        }
        return enumeratedPath;
    }
}
