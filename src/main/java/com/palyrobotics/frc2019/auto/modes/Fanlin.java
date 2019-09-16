package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.*;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCloseRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersOpenRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.config.Constants.PhysicalConstants;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")

public class Fanlin extends AutoModeBase {
    //right start > rocket ship close > loading station > rocket ship far > depot > rocket ship mid

    public static int kRunSpeed = 100;
    //    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetX = -20 - 25;
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
    public static double kRightFirstCargoShipX = kCargoShipRightFrontX + mDistances.kCargoOffsetX;
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
        return new SequentialRoutine(new RightStartRightFrontCargoAutoMode().getRoutine(), takeHatch(), placeHatch());
    }

    public Routine takeHatch() { //cargo ship front to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> BackCargoShipToLoadingStation = new ArrayList<>();
        //backs out of the cargo ship
        BackCargoShipToLoadingStation.add(new Waypoint(kCargoShipRightFront.translateBy
                (new Translation2d(-PhysicalConstants.kRobotLengthInches, 0)), kRunSpeed));

        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .45 + PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetX,
                kRightLoadingStationY * .5 + kOffsetY), kRunSpeed));
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .45 - PhysicalConstants.kRobotLengthInches * 0.5 + kOffsetX,
                kRightLoadingStationY + PhysicalConstants.kRobotLengthInches * 0.1 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(BackCargoShipToLoadingStation), true));

        //turn toward the loading station
        routines.add(new BBTurnAngleRoutineRequireVision(90));

        //drive toward the loading station and align with vision and pusher out
        routines.add(new ParallelRoutine(new VisionClosedDriveRoutine(), new PusherOutRoutine()));

        //latch on to hatch
        routines.add(new FingersOpenRoutine());
        //wait
        routines.add(new TimeoutRoutine(.2));
        //pusher back in
        routines.add(new PusherInRoutine());

        return new SequentialRoutine(routines);
    }

    public Routine placeHatch() {
        ArrayList<Routine> routines = new ArrayList<>();

        routines.add(new DriveSensorResetRoutine(1));

        ArrayList<Waypoint> BackLoadingStationToCargoShip = new ArrayList<>();
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kHabLineX,
                0), kRunSpeed * 1.25));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kRightFirstCargoShipX * 0.6,
                -40), kRunSpeed));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(-kRightFirstCargoShipX - PhysicalConstants.kRobotLengthInches * 0.1,
                -50), 0));

        routines.add(new DrivePathRoutine(new Path(BackLoadingStationToCargoShip), true));

        routines.add(new TimeoutRoutine(.3));

        routines.add(new BBTurnAngleRoutine(-85));

        routines.add(new TimeoutRoutine(.15));

        //go to cargoship bay while using vision to align
        routines.add(new ParallelRoutine(new VisionClosedDriveRoutine(), new PusherOutRoutine()));


        //release hatch
        routines.add(new FingersCloseRoutine());
        routines.add(new FingersExpelRoutine(.05));

        //wait
        routines.add(new TimeoutRoutine(.4));

        //pusher back in
        routines.add(new PusherInRoutine());

        return new SequentialRoutine(routines);

    }


    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}