package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar on 4/5/17.
 */
public class DrivePathRoutine extends Routine {
    private Path mPath;
    private ArrayList<Path.Waypoint> mPathList;
    private double mLookAhead;
    private double mStartSpeed;
    private boolean mInverted;
    private double mTolerance;
    private boolean mRelative;

    /**
     * @param path     Path to follow
     * @param inverted Whether or not to drive backwards
     */
    public DrivePathRoutine(Path path, boolean inverted) {
        mPath = path;
        mLookAhead = DrivetrainConstants.kPathFollowingLookahead;
        mStartSpeed = 0.0;
        mInverted = inverted;
        mTolerance = DrivetrainConstants.kPathFollowingTolerance;
        mRelative = false;
    }

    /**
     * @param path      the path to follow
     * @param lookAhead the lookahead distance desired
     * @param inverted  whether or not to drive backwards
     */
    public DrivePathRoutine(Path path, boolean inverted, double lookAhead) {
        mPath = path;
        mLookAhead = lookAhead;
        mStartSpeed = 0.0;
        mInverted = inverted;
        mTolerance = DrivetrainConstants.kPathFollowingTolerance;
        mRelative = false;
    }

    public DrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, double startSpeed) {
        mPath = new Path(pathList);
        mPathList = pathList;
        mInverted = inverted;
        mStartSpeed = startSpeed;
        mLookAhead = DrivetrainConstants.kPathFollowingLookahead;
        mTolerance = DrivetrainConstants.kPathFollowingTolerance;
        mRelative = false;
    }

    public DrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, double startSpeed, double lookahead) {
        mPath = new Path(pathList);
        mPathList = pathList;
        mInverted = inverted;
        mStartSpeed = startSpeed;
        mLookAhead = lookahead;
        mTolerance = DrivetrainConstants.kPathFollowingTolerance;
        mRelative = false;
    }

    public DrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, double startSpeed, double lookahead, double tolerance) {
        mPath = new Path(pathList);
        mPathList = pathList;
        mInverted = inverted;
        mStartSpeed = startSpeed;
        mLookAhead = lookahead;
        mTolerance = tolerance;
        mRelative = false;
    }

    public DrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, double startSpeed, double lookahead, double tolerance, boolean isRelative) {
        mPath = new Path(new ArrayList<>());
        mPathList = pathList;
        mInverted = inverted;
        mStartSpeed = startSpeed;
        mLookAhead = lookahead;
        mTolerance = tolerance;
        mRelative = isRelative;
    }

    public DrivePathRoutine(ArrayList<Path.Waypoint> pathList, boolean inverted, boolean relative) {
        mPath = new Path(new ArrayList<>());
        mPathList = pathList;
        mInverted = inverted;
        mLookAhead = DrivetrainConstants.kPathFollowingLookahead;
        mStartSpeed = 0.0;
        mRelative = relative;
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
        mDrive.setTrajectoryController(mPath, mLookAhead, mInverted, mTolerance);
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
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
        return "DrivePathRoutine";
    }

    @Override
    public String toString() {
        final int offsetX = 0;
        final int offsetY = 0;
        StringBuilder enumeratedPath = new StringBuilder();
        List<Path.Waypoint> path = mPath.getWayPoints();
        enumeratedPath.append("0,0,0\n");
		for (Path.Waypoint wayPoint : path) {
			enumeratedPath.append(wayPoint.position.getX() + offsetX).append(",").append(wayPoint.position.getY() + offsetY).append(",").append(wayPoint.speed).append("\n");
		}
        return enumeratedPath.toString();
    }
}
