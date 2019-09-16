package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCloseRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersOpenRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.Constants.PhysicalConstants;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class LeftFullLevelAutoMode extends AutoModeBase {
    //Left start > rocket ship close > loading station > rocket ship far > depot > rocket ship mid
    //TODO: copy the right side one

    public static int kRunSpeed = 70;
    public static double kOffsetX = PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 - PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipLeftFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipLeftFrontY = mDistances.kFieldWidth * .5 - (mDistances.kCargoLeftY + mDistances.kCargoOffsetY);
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kLeftLoadingStationX = 0;
    public static double kLeftLoadingStationY = mDistances.kFieldWidth * .5 - mDistances.kLeftLoadingY;
    public static double kLeftDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kLeftDepotY = mDistances.kFieldWidth * .5 - mDistances.kDepotFromLeftY;
    public static double kLeftRocketShipCloseX = mDistances.kHabLeftRocketCloseX + kHabLineX;
    public static double kLeftRocketShipCloseY = mDistances.kFieldWidth * .5 - mDistances.kLeftRocketCloseY;
    public static double kLeftRocketShipMidX = kHabLineX + mDistances.kHabLeftRocketMidX;
    public static double kLeftRocketShipMidY = mDistances.kFieldWidth * .5 - mDistances.kLeftRocketMidY;
    public static double kLeftRocketShipFarX = mDistances.kFieldWidth - mDistances.kMidlineLeftRocketFarX;
    public static double kLeftRocketShipFarY = mDistances.kFieldWidth * .5 - mDistances.kLeftRocketFarY;

    public Translation2d kCargoShipLeftFront = new Translation2d(kCargoShipLeftFrontX + PhysicalConstants.kRobotWidthInches * .2 + kOffsetX,
            kCargoShipLeftFrontY - PhysicalConstants.kRobotLengthInches * .05 + kOffsetY);
    public Translation2d kLeftLoadingStation = new Translation2d(kLeftLoadingStationX + PhysicalConstants.kRobotLengthInches + kOffsetX,
            kLeftLoadingStationY - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY);
    public Translation2d kLeftRocketShipFar = new Translation2d(kLeftRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1 + kOffsetX,
            kLeftRocketShipFarY - PhysicalConstants.kRobotLengthInches * .05 + kOffsetY);
    public Translation2d kLeftDepot = new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
            kLeftDepotY - PhysicalConstants.kRobotLengthInches * .25 + kOffsetY);
    public Translation2d kLeftRocketShipClose = new Translation2d(kLeftRocketShipCloseX + PhysicalConstants.kRobotLengthInches * .3 + kOffsetX,
            kLeftRocketShipCloseY - PhysicalConstants.kRobotLengthInches * .5 + kOffsetY);

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new RezeroSubAutoMode().Rezero(true), placeHatchClose(), takeHatch(), placeHatchFar(), takeCargo(), placeCargoMid());
    }

    public Routine placeHatchClose() { //start to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> DepotToRocketShip = new ArrayList<>();
        DepotToRocketShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                0), 100)); //goes straight at the start so the robot doesn't get messed up over the ramp
        DepotToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipCloseX * .8 + kOffsetX,
                findLineClose(kLeftRocketShipCloseX * .8) + kOffsetY), kRunSpeed)); //line up with rocket ship
        DepotToRocketShip.add(new Waypoint(kLeftRocketShipClose, 0));
        //move elevator up while driving
        //routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToRocketShip), true),
        //        new ElevatorCustomPositioningRoutine(, 1)));

        //place hatch with auto placer

        return new SequentialRoutine(routines);
    }

    public Routine takeHatch() { //rocket ship close to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> CargoShipToLoadingStation = new ArrayList<>();
        CargoShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kLeftLoadingStationY - PhysicalConstants.kRobotWidthInches * .4 + kOffsetY), kRunSpeed)); //lines up with loading station
        CargoShipToLoadingStation.add(new Waypoint(kLeftLoadingStation, 0));

        //get fingers ready for hatch intake
        ArrayList<Routine> getIntakeReady = new ArrayList<>();
        getIntakeReady.add(new PusherOutRoutine());
        getIntakeReady.add(new FingersCloseRoutine());

        //move elevators while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToLoadingStation), false),
                new SequentialRoutine(getIntakeReady)));

        //intake hatch
        routines.add(new PusherInRoutine());
        routines.add(new FingersOpenRoutine());
        return new SequentialRoutine(routines);
    }

    public Routine placeHatchFar() { //loading station to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts backwards at the loading station after loading a hatch. It then goes around the rocket ship and over shoots it a bit. Then, it lines up with the rocket ship far and places the hatch.
         */

        List<Waypoint> backLoadingStationToRocketShip = new ArrayList<>(); //robot starts going backwards
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftLoadingStationX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kLeftLoadingStationY + kOffsetY), 180)); //backs up a bit
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipMidX + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.8) + kOffsetY), kRunSpeed)); //goes around the rocket ship
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.9 + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.9) - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(backLoadingStationToRocketShip), true)); //robot turns and then moves forward to the rocket ship

        List<Waypoint> forwardLoadingStationToRocketShip = new ArrayList<>(); //robot turns and then moves forward
        forwardLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.6 + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) - PhysicalConstants.kRobotLengthInches * .3 + kOffsetY), kRunSpeed)); //line up with rocket ship far
        forwardLoadingStationToRocketShip.add(new Waypoint(kLeftRocketShipFar, 0)); //ends in front of the rocket ship far
        //move elevator up while driving
        //routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(forwardLoadingStationToRocketShip), false),
        //       new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorRocketHatchHeight1Inches, 1)));

        //place hatch
        routines.add(new FingersCycleRoutine(1));

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo() { //rocket ship close to depot
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts at rocket ship far and goes around the rocket backwards. Ends at the depot and loads a cargo.
         */

        List<Waypoint> RocketShipToDepot = new ArrayList<>();
        RocketShipToDepot.add(new Waypoint(new Translation2d(kLeftRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.6 + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) + PhysicalConstants.kRobotLengthInches * 0.5 + kOffsetY), kRunSpeed));
        RocketShipToDepot.add(new Waypoint(new Translation2d(kCargoShipLeftFrontX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kLeftDepotY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), kRunSpeed)); //turn around the rocket ship
        RocketShipToDepot.add(new Waypoint(kLeftDepot, 0));
        //move elevator down while driving
        //routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(RocketShipToDepot), true),
        ///        new ElevatorCustomPositioningRoutine(ElevatorConstants.kBotomPositionInches, 1)));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargoMid() { //depot to rocket ship mid
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> DepotToRocketShipMid = new ArrayList<>();
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kLeftDepotY + kOffsetY), kRunSpeed)); //go straight until near rocket ship
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(kLeftRocketShipMidX - PhysicalConstants.kRobotLengthInches * .3,
                kLeftDepotY + kOffsetY), kRunSpeed));
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(kLeftRocketShipMidX,
                kLeftRocketShipMidY - PhysicalConstants.kRobotLengthInches * .8), kRunSpeed)); //line up with rocket ship
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(kLeftRocketShipMidX,
                kLeftRocketShipMidY), 0));
        //move elevator up while driving
        //routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToRocketShipMid), false),
        //        new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorRocketCargoHeight1Inches, 1)));

        //shoot cargo
        routines.add(new PusherOutRoutine());
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 1));

        return new SequentialRoutine(routines);
    }

    public double findLineClose(double cordX) {
        return 0.54862 * cordX - 0.54862 * kLeftRocketShipCloseX + kLeftRocketShipCloseY; //slope is derived from the angle of the rocket ship sides - constants derived from math
    } //the y cord of an invisible line extending from rocket ship close

    public double findLineFar(double cordX) {
        return -0.54862 * cordX + 0.54862 * kLeftRocketShipFarX + kLeftRocketShipFarY; //slope is derived from the angle of the rocket ship sides - constants derived from math
    } //the y cord of an invisible line extending from rocket ship close

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}