package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersOpenRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Constants.PhysicalConstants;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class RightFullLevelAutoMode extends AutoModeBase {
    //right start > rocket ship close > loading station > rocket ship far > depot > rocket ship mid

    public static int kRunSpeed = 60;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches * 0.6;
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
        return new SequentialRoutine(new RezeroSubAutoMode().Rezero(true), placeHatchClose(), takeHatch(), placeHatchFar(), takeCargo(), placeCargoMid());
    }

    public Routine placeHatchClose() { //start to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> DepotToRocketShip = new ArrayList<>();
        DepotToRocketShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                0), kRunSpeed)); //goes straight at the start so the robot doesn't get messed up over the ramp
        DepotToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipCloseX * .8 + kOffsetX,
                findLineClose(kRightRocketShipCloseX * .8) + kOffsetY), kRunSpeed)); //line up with rocket ship
        DepotToRocketShip.add(new Waypoint(kRightRocketShipClose, 0));
        //move elevator up while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToRocketShip), false),
                new ElevatorCustomPositioningRoutine(OtherConstants.kRocketHatchTargetHeight, 1)));

        ArrayList<Waypoint> goForward = new ArrayList<>();
        goForward.add(new Waypoint(new Translation2d(0, 0), 20, true));
        //TODO: change translation cords
        goForward.add(new Waypoint(new Translation2d(20, 0), 0, true));

        //pusher out while driving forward slowly
        routines.add(new ParallelRoutine(new DrivePathRoutine(goForward, false, true),
                new PusherOutRoutine()));

        //release hatch
        routines.add(new FingersOpenRoutine());

        //pusher back in
        routines.add(new PusherInRoutine());

        return new SequentialRoutine(routines);
    }

    public Routine takeHatch() { //rocket ship close to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> BackCargoShipToLoadingStation = new ArrayList<>();
        BackCargoShipToLoadingStation.add(new Waypoint(kRightRocketShipClose, kRunSpeed));
        BackCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation.translateBy
                (new Translation2d(PhysicalConstants.kRobotLengthInches * 2, -PhysicalConstants.kRobotLengthInches * 0.5)), 0));
        routines.add(new DrivePathRoutine(new Path(BackCargoShipToLoadingStation), true));

        //turn to face the loading station
        routines.add(new CascadingGyroEncoderTurnAngleRoutine(-160));

        List<Waypoint> ForwardCargoShipToLoadingStation = new ArrayList<>();
        ForwardCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation.translateBy
                (new Translation2d(PhysicalConstants.kRobotLengthInches * 1.5, 0)), kRunSpeed));
        ForwardCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation, 0));

        //get pusher ready for hatch intake
        ArrayList<Routine> getIntakeReady = new ArrayList<>();
        getIntakeReady.add(new PusherOutRoutine());

        //drive and ready pusher at the same time
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(ForwardCargoShipToLoadingStation), false),
                new SequentialRoutine(getIntakeReady)));

//        ArrayList<Waypoint> goForwardABit = new ArrayList<>();
//        goForwardABit.add(new Waypoint(new Translation2d(0, 0), 20, true));
//        goForwardABit.add(new Waypoint(new Translation2d(-20, 0), 0, true));
//
//        //drive slowly forward and intake hatch
//        routines.add(new SequentialRoutine(new DrivePathRoutine(goForwardABit, false, true),
//                new FingersOpenRoutine(), new PusherInRoutine()));

        return new SequentialRoutine(routines);
    }

    public Routine placeHatchFar() { //loading station to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts backwards at the loading station after loading a hatch. It then goes around the rocket ship and over shoots it a bit. Then, it lines up with the rocket ship far and places the hatch.
         */

        List<Waypoint> backLoadingStationToRocketShip = new ArrayList<>(); //robot starts going backwards
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightLoadingStationX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightLoadingStationY + kOffsetY), kRunSpeed)); //backs up a bit
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipMidX + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.8) + kOffsetY), kRunSpeed)); //goes around the rocket ship
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.9 + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.9) - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));

        routines.add(new DrivePathRoutine(new Path(backLoadingStationToRocketShip), true)); //robot turns and then moves forward to the rocket ship

        List<Waypoint> forwardLoadingStationToRocketShip = new ArrayList<>(); //robot turns and then moves forward
        forwardLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.6 + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) - PhysicalConstants.kRobotLengthInches * .3 + kOffsetY), kRunSpeed)); //line up with rocket ship far
        forwardLoadingStationToRocketShip.add(new Waypoint(kRightRocketShipFar, 0)); //ends in front of the rocket ship far

        routines.add(new DrivePathRoutine(new Path(forwardLoadingStationToRocketShip), false));

//        ArrayList<Waypoint> goForward = new ArrayList<>();
//        goForward.add(new Waypoint(new Translation2d(0, 0), 20, true));
//        //TODO: change translation cords
//        goForward.add(new Waypoint(new Translation2d(20, 0), 0, true));

//        //pusher out while driving forward slowly
//        routines.add(new ParallelRoutine(new DrivePathRoutine(goForward, false, true),
//                new PusherOutRoutine()));

        //release hatch
        routines.add(new FingersOpenRoutine());

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo() { //rocket ship close to depot - could be more accurate
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts at rocket ship far and goes around the rocket backwards. Ends at the depot and loads a cargo.
         */

        List<Waypoint> RocketShipToDepot = new ArrayList<>();
        RocketShipToDepot.add(new Waypoint(new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.6 + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) + PhysicalConstants.kRobotLengthInches * 0.5 + kOffsetY), 70));
        RocketShipToDepot.add(new Waypoint(new Translation2d(kCargoShipRightFrontX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 180)); //turn around the rocket ship
        RocketShipToDepot.add(new Waypoint(kRightDepot, 0));
        //move elevator down while driving
        routines.add(new DrivePathRoutine(new Path(RocketShipToDepot), true));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargoMid() { //depot to rocket ship mid
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> DepotToRocketShipMid = new ArrayList<>();
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kRightDepotY + kOffsetY), kRunSpeed)); //go straight until near rocket ship
        DepotToRocketShipMid.add(new Waypoint(new Translation2d(kRightRocketShipMidX - PhysicalConstants.kRobotLengthInches * .4,
                kRightDepotY + kOffsetY), kRunSpeed));
        DepotToRocketShipMid.add(new Waypoint(kRightRocketShipMid.translateBy
                (new Translation2d(0, PhysicalConstants.kRobotLengthInches * .6)), kRunSpeed)); //line up with rocket ship
        DepotToRocketShipMid.add(new Waypoint(kRightRocketShipMid, 0));

        //move elevator up while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToRocketShipMid), false),
                new ElevatorCustomPositioningRoutine(Configs.get(ElevatorConfig.class).elevatorHeight1, 1)));

        //shoot cargo
        routines.add(new PusherOutRoutine());
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 1));

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