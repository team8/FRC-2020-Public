package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.VisionAssistedDrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCloseRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersOpenRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.*;
import com.palyrobotics.frc2019.behavior.routines.pusher.*;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class Fanlin extends AutoModeBase {
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
        return new SequentialRoutine(new CenterStartRightFrontCargoAutoMode().getRoutine(), takeHatch(), placeHatch());
    }

    public Routine takeHatch() { //cargo ship front to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> BackCargoShipToLoadingStation = new ArrayList<>();
        BackCargoShipToLoadingStation.add(new Waypoint(kCargoShipRightFront.translateBy
                (new Translation2d(-PhysicalConstants.kRobotLengthInches, 0)), kRunSpeed)); //backs out of the cargo ship
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .5 + kOffsetX,
                kRightLoadingStationY * .5 + kOffsetY), kRunSpeed));
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .5 - PhysicalConstants.kRobotLengthInches * 0.7 + kOffsetX,
                kRightLoadingStationY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(BackCargoShipToLoadingStation), true));

        //turn toward the loading station
        routines.add(new CascadingGyroEncoderTurnAngleRoutine(90));

        ArrayList<Waypoint> ForwardCargoShipToLoadingStation = new ArrayList<>();
        ForwardCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kRightLoadingStationY + PhysicalConstants.kRobotWidthInches * .4 + kOffsetY), kRunSpeed));
        ForwardCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation.translateBy
                (new Translation2d(PhysicalConstants.kRobotLengthInches, 0)), 0));

        //get fingers ready for hatch intake
        ArrayList<Routine> getIntakeReady = new ArrayList<>();
        getIntakeReady.add(new PusherOutRoutine());
        getIntakeReady.add(new FingersCloseRoutine());

        //drive and ready fingers at the same time
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(ForwardCargoShipToLoadingStation), false),
                new SequentialRoutine(getIntakeReady)));

        ArrayList<Waypoint> goForwardABit = new ArrayList<>();
        goForwardABit.add(new Waypoint(kRightLoadingStation.translateBy
                (new Translation2d(PhysicalConstants.kRobotLengthInches, 0)), 20));
        goForwardABit.add(new Waypoint(kRightLoadingStation, 0));

        //drive slowly forward and intake hatch
        routines.add(new SequentialRoutine(new DrivePathRoutine(new Path(goForwardABit), false),
                new FingersOpenRoutine(), new PusherInRoutine()));

        return new SequentialRoutine(routines);
    }

    public Routine placeHatch() {
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> DepotToCargoShip = new ArrayList<>();
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kRightDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .55 + kOffsetX,
                kRightDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + kOffsetX,
                kRightFirstCargoShipY + PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed, "visionStart")); //line up in front of cargo bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + kOffsetX,
                kRightFirstCargoShipY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));

        routines.add(new VisionAssistedDrivePathRoutine(DepotToCargoShip, false, false, "visionStart"));

        //move elevator up while driving
//        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToCargoShip), false),
//                new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorCargoBaysHeightInches, 1)));

        return new SequentialRoutine(routines);

    }



    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}