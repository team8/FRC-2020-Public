package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.*;
import com.palyrobotics.frc2019.config.Constants.PhysicalConstants;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")

public class TwoSide extends AutoModeBase {
    //right start > rocket ship close > loading station > rocket ship far > depot > rocket ship mid

    public static int kRunSpeed = 60;
    public static double kOffsetX = 10;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kRightLoadingStationX = 0;
    public static double kRightLoadingStationY = -(mDistances.kFieldWidth * .5 - mDistances.kRightLoadingY);
    public static double kRightDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kRightDepotY = -(mDistances.kFieldWidth * .5 - mDistances.kDepotFromRightY);
    public static double kRightRocketShipCloseX = mDistances.kHabRightRocketCloseX + kHabLineX;
    public static double kRightRocketShipCloseY = -(mDistances.kFieldWidth * .5 - mDistances.kRightRocketCloseY);
    public static double kRightRocketShipMidX = kHabLineX + mDistances.kHabRightRocketMidX;
    public static double kRightRocketShipMidY = -(mDistances.kFieldWidth * .5 - mDistances.kRightRocketMidY);
    public static double kRightRocketShipFarX = mDistances.kFieldWidth - mDistances.kMidlineRightRocketFarX;
    public static double kRightRocketShipFarY = -(mDistances.kFieldWidth * .5 - mDistances.kRightRocketFarY);
    public static double kRightFirstCargoShipX = kCargoShipRightFrontX + mDistances.kCargoOffsetY;
    public static double kRightFirstCargoShipY = mDistances.kFieldWidth * .5 - mDistances.kCargoRightY;


    public Translation2d kCargoShipRightFront = new Translation2d(kCargoShipRightFrontX + PhysicalConstants.kRobotWidthInches * .2 + kOffsetX,
            kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * .05 + kOffsetY);
    public Translation2d kRightLoadingStation = new Translation2d(kRightLoadingStationX + PhysicalConstants.kRobotLengthInches + kOffsetX,
            kRightLoadingStationY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY);
    public Translation2d kRightRocketShipFar = new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1 + kOffsetX,
            kRightRocketShipFarY + PhysicalConstants.kRobotLengthInches * .05 + kOffsetY);
    public Translation2d kRightDepot = new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
            kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY);
    public Translation2d kRightRocketShipClose = new Translation2d(kRightRocketShipCloseX + PhysicalConstants.kRobotLengthInches * .3 + kOffsetX,
            kRightRocketShipCloseY + PhysicalConstants.kRobotLengthInches * .5 + kOffsetY);
    public Translation2d kRightRocketShipMid = new Translation2d(kRightRocketShipMidX + kOffsetX,
            kRightRocketShipMidY + kOffsetY);

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(1), placeHatch1(), takeHatch(), placeHatch2());
    }

    public Routine placeHatch1() {
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX,
                0), kRunSpeed));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .55 + kOffsetX,
                0 + kOffsetY), kRunSpeed));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed, "visionStart")); //line up in front of cargo bay
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));

        routines.add(new VisionAssistedDrivePathRoutine(StartToCargoShip, false, false, "visionStart"));

        //move elevator up while driving
//        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(StartToCargoShip), false),
//                new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorCargoBaysHeightInches, 1)));

        return new SequentialRoutine(routines);

    }

    public Routine takeHatch() { //cargo ship front to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> BackCargoShipToLoadingStation = new ArrayList<>();
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed)); //backs out of the cargo ship
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .5 + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightLoadingStationY * .5 + kOffsetY), kRunSpeed));
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .5 + kOffsetX,
                kRightLoadingStationY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(BackCargoShipToLoadingStation), true));

        //turn toward the loading station
        routines.add(new CascadingGyroEncoderTurnAngleRoutine(90));

        ArrayList<Waypoint> ForwardCargoShipToLoadingStation = new ArrayList<>();
        ForwardCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kRightLoadingStationY + PhysicalConstants.kRobotWidthInches * .4 + kOffsetY), kRunSpeed));
        ForwardCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation.translateBy
                (new Translation2d(PhysicalConstants.kRobotLengthInches, 0)), kRunSpeed, "visionStart"));
        ForwardCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation, 0));

        routines.add(new VisionAssistedDrivePathRoutine(ForwardCargoShipToLoadingStation,
                false, false, "visionStart"));

        return new SequentialRoutine(routines);
    }

    public Routine placeHatch2() {
        ArrayList<Routine> routines = new ArrayList<>();

        routines.add(new DriveSensorResetRoutine(1));

        ArrayList<Waypoint> BackLoadingStationToCargoShip = new ArrayList<>();
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(0, 0), kRunSpeed));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kHabLineX,
                0), kRunSpeed));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kHabLineX,
                0), kRunSpeed));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kRightFirstCargoShipX * 0.8,
                -40), kRunSpeed));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kRightFirstCargoShipX - PhysicalConstants.kRobotLengthInches * 0.5,
                -40)));

        routines.add(new DrivePathRoutine(new Path(BackLoadingStationToCargoShip), true));

        routines.add(new BBTurnAngleRoutine(-90));

        routines.add(new DriveSensorResetRoutine(1));

        ArrayList<Waypoint> ForwardLoadingStationToCargoShip = new ArrayList<>();
        ForwardLoadingStationToCargoShip.add(new Waypoint(new Translation2d(0,
                0), kRunSpeed, "visionStart")); //line up in front of cargo bay
        ForwardLoadingStationToCargoShip.add(new Waypoint(new Translation2d(0, 50), 0));

        routines.add(new VisionAssistedDrivePathRoutine(ForwardLoadingStationToCargoShip, false, false, "visionStart"));

        return new SequentialRoutine(routines);
    }


    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}