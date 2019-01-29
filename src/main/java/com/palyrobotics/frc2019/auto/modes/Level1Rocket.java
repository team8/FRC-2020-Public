package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.behavior.routines.drive.*;

import java.util.ArrayList;
import java.util.List;

public class Level1Rocket extends AutoModeBase { //right start > rocket ship close > loading station > rocket ship far > depot > rocket ship mid

    //TODO: tune the code - I haven't tested yet

    public static int SPEED = 120;
    public static double kOffsetX = -Constants.kLowerPlatformLength - Constants.kRobotLengthInches;
    public static double kOffsetY = Constants.kLevel3Width * .5 + Constants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + Constants.kLowerPlatformLength + Constants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
    public static double kHabLineX = Constants.kUpperPlatformLength + Constants.kLowerPlatformLength;
    public static double kRightLoadingStationX = 0;
    public static double kRightLoadingStationY = -(mDistances.kFieldWidth * .5 - mDistances.kRightLoadingY);
    public static double kRightDepotX = Constants.kUpperPlatformLength;
    public static double kRightDepotY = -(mDistances.kFieldWidth * .5 - mDistances.kDepotFromRightY);
    public static double kRightRocketShipCloseX = mDistances.kHabRightRocketCloseX + kHabLineX;
    public static double kRightRocketShipCloseY = -(mDistances.kFieldWidth * .5 - mDistances.kRightRocketCloseY);
    public static double kRightRocketShipMidX = kHabLineX + mDistances.kHabRightRocketMidX;
    public static double kRightRocketShipMidY = -(mDistances.kFieldWidth * .5 - mDistances.kRightRocketMidY);
    public static double kRightRocketShipFarX = mDistances.kFieldWidth - mDistances.kMidlineRightRocketFarX;
    public static double kRightRocketShipFarY = -(mDistances.kFieldWidth * .5 - mDistances.kRightRocketFarY);

    public Translation2d kCargoShipRightFront = new Translation2d(-(kCargoShipRightFrontX + Constants.kRobotWidthInches * .2 + kOffsetX), -(kCargoShipRightFrontY + Constants.kRobotLengthInches * .05 + kOffsetY));
    public Translation2d kRightLoadingStation = new Translation2d(-(kRightLoadingStationX + Constants.kRobotLengthInches + kOffsetX), -(kRightLoadingStationY + Constants.kRobotLengthInches * .2 + kOffsetY));
    public Translation2d kRightRocketShipFar = new Translation2d(-(kRightRocketShipFarX + Constants.kRobotLengthInches * 1 + kOffsetX), -(kRightRocketShipFarY + Constants.kRobotLengthInches * .05 + kOffsetY));
    public Translation2d kRightDepot = new Translation2d(-(kRightDepotX + Constants.kRobotLengthInches * 1.1 + kOffsetX), -(kRightDepotY + Constants.kRobotLengthInches * .25 + kOffsetY));
    public Translation2d kRightRocketShipClose = new Translation2d(-(kRightRocketShipCloseX + Constants.kRobotLengthInches * .3 + kOffsetX), -(kRightRocketShipCloseY + Constants.kRobotLengthInches * .5 + kOffsetY));

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(0.2), new Rezero().getRoutine(), placeHatchClose(), takeHatch(), placeHatchFar(), takeCargo(), placeCargoMid());
    }

    public Routine placeHatchClose() { //start to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

//        TODO: make super accurate (can only be at most 2 inches off)

        List<Path.Waypoint> DepotToRocketShip = new ArrayList<>();
        DepotToRocketShip.add(new Waypoint(new Translation2d(-(kHabLineX + Constants.kRobotLengthInches + kOffsetX), 0), 100)); //goes straight at the start so the robot doesn't get messed up over the ramp
        DepotToRocketShip.add(new Waypoint(new Translation2d(-(kRightRocketShipCloseX * .8 + kOffsetX), -(findLineClose(kRightRocketShipCloseX * .8) + kOffsetY)), 180));
        DepotToRocketShip.add(new Waypoint(kRightRocketShipClose, 0));
        routines.add(new DrivePathRoutine(new Path(DepotToRocketShip), true));

//        TODO: add ReleaseHatchRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine takeHatch() { //rocket ship close to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToLoadingStation = new ArrayList<>();
        CargoShipToLoadingStation.add(new Waypoint(new Translation2d(-(kHabLineX - Constants.kRobotLengthInches * .5 + kOffsetX), -(kRightLoadingStationY + Constants.kRobotWidthInches * .4 + kOffsetY)), SPEED)); //lines up with loading station
        CargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation, 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToLoadingStation), false));

//        TODO: add IntakeHatchRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine placeHatchFar() { //loading station to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts backwards at the loading station after loading a hatch. It then goes around the rocket ship and over shoots it a bit. Then, it lines up with the rocket ship far and places the hatch.
         */

        List<Path.Waypoint> backLoadingStationToRocketShip = new ArrayList<>(); //robot starts going backwards
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(-(kRightLoadingStationX + Constants.kRobotLengthInches + kOffsetX), -(kRightLoadingStationY + kOffsetY)), 180)); //backs up a bit
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(-(kRightRocketShipMidX + kOffsetX), -(findLineFar(kRightRocketShipMidX + Constants.kRobotLengthInches * 1.8) + kOffsetY)), SPEED)); //goes around the rocket ship
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(-(kRightRocketShipFarX + Constants.kRobotLengthInches * 1.9 + kOffsetX), -(findLineFar(kRightRocketShipMidX + Constants.kRobotLengthInches * 1.9) - Constants.kRobotLengthInches * .2 + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(backLoadingStationToRocketShip), true)); //robot turns and then moves forward to the rocket ship

//        routines.add(new TimeoutRoutine(20));

        List<Path.Waypoint> forwardLoadingStationToRocketShip = new ArrayList<>(); //robot turns and then moves forward
        forwardLoadingStationToRocketShip.add(new Waypoint(new Translation2d(-(kRightRocketShipFarX + Constants.kRobotLengthInches * 1.6 + kOffsetX), -(findLineFar(kRightRocketShipMidX + Constants.kRobotLengthInches * 1.6) - Constants.kRobotLengthInches * .3 + kOffsetY)), SPEED)); //line up with rocket ship far
        forwardLoadingStationToRocketShip.add(new Waypoint(kRightRocketShipFar, 0)); //ends in front of the rocket ship far
        routines.add(new DrivePathRoutine(new Path(forwardLoadingStationToRocketShip), false));

//        TODO: add ReleaseHatchRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo() { //rocket ship close to depot - could be more accurate
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts at rocket ship far and goes around the rocket backwards. Ends at the depot and loads a cargo.
         */

        List<Path.Waypoint> RocketShipToDepot = new ArrayList<>();
        RocketShipToDepot.add(new Waypoint(new Translation2d(-(kRightRocketShipFarX + Constants.kRobotLengthInches * 1.6 + kOffsetX), -(findLineFar(kRightRocketShipMidX + Constants.kRobotLengthInches * 1.6) + Constants.kRobotLengthInches * 0.5 + kOffsetY)), 70));
        RocketShipToDepot.add(new Waypoint(new Translation2d(-(kCargoShipRightFrontX + Constants.kRobotLengthInches + kOffsetX), -(kRightDepotY + Constants.kRobotLengthInches * .2 + kOffsetY)), 180));
        RocketShipToDepot.add(new Waypoint(kRightDepot, 0));
        routines.add(new DrivePathRoutine(new Path(RocketShipToDepot), true));

//        TODO: add IntakeCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine placeCargoMid() {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToRocketShipMid = new ArrayList<>();
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(-(kRightDepotX + Constants.kRobotLengthInches * 2 + kOffsetX), -(kRightDepotY + kOffsetY)), SPEED));
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(-(kRightRocketShipMidX - Constants.kRobotLengthInches * .3), -(kRightDepotY + kOffsetY)), SPEED));
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(-(kRightRocketShipMidX), -(kRightRocketShipMidY + Constants.kRobotLengthInches * .8)), SPEED));
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(-(kRightRocketShipMidX), -(kRightRocketShipMidY)), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToRocketShipMid), false));

//        TODO: add ReleaseCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public double findLineClose(double cordX) {
        return -0.54862 * cordX + 0.54862 * kRightRocketShipCloseX + kRightRocketShipCloseY; //slope is derived from the angle of the rocket ship sides - constants derived from math
    } //the y cord of an invisible line extending from rocket ship close

    public double findLineFar(double cordX) {
        return 0.54862 * cordX - 0.54862 * kRightRocketShipFarX + kRightRocketShipFarY; //slope is derived from the angle of the rocket ship sides - constants derived from math
    } //the y cord of an invisible line extending from rocket ship close

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}