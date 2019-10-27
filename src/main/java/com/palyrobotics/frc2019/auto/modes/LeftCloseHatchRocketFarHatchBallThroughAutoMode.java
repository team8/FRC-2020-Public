package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.constants.PhysicalConstants;
import com.palyrobotics.frc2019.config.subsystem.ElevatorConfig;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class LeftCloseHatchRocketFarHatchBallThroughAutoMode extends AutoModeBase {
    //Left start > cargo ship front > loading station > rocket ship far > depot > close rocket ship (fullsend)
    //TODO: copy the right side one

    public static int kRunSpeed = 50;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches * 0.6;
    public static double kOffsetY = -PhysicalConstants.kLevel3Width * .5 - PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipLeftFrontX = sDistances.level1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipLeftFrontY = sDistances.fieldWidth * .5 - (sDistances.cargoLeftY + sDistances.cargoOffsetY);
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kLeftLoadingStationX = 0;
    public static double kLeftLoadingStationY = sDistances.fieldWidth * .5 - sDistances.leftLoadingY;
    public static double kLeftDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kLeftDepotY = sDistances.fieldWidth * .5 - sDistances.depotFromLeftY;
    public static double kLeftRocketShipCloseX = sDistances.habLeftRocketCloseX + kHabLineX;
    public static double kLeftRocketShipCloseY = sDistances.fieldWidth * .5 - sDistances.leftRocketCloseY;
    public static double kLeftRocketShipMidX = kHabLineX + sDistances.habLeftRocketMidX;
    public static double kLeftRocketShipMidY = sDistances.fieldWidth * .5 - sDistances.leftRocketMidY;
    public static double kLeftRocketShipFarX = sDistances.fieldWidth - sDistances.midLineLeftRocketFarX;
    public static double kLeftRocketShipFarY = sDistances.fieldWidth * .5 - sDistances.leftRocketFarY;

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
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new LeftStartLeftFrontCargoAutoMode().placeHatch(), takeHatch(), placeHatch2(), takeCargo(), placeCargoClose());
    }

    //TODO: make legit
    public Routine takeHatch() { //cargo ship front to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> BackCargoShipToLoadingStation = new ArrayList<>();
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipLeftFront.translateBy(
                new Translation2d(0, PhysicalConstants.kRobotLengthInches))), kRunSpeed)); //backs out of the cargo ship
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d((kCargoShipLeftFrontX * .5 + kOffsetX),
                kLeftLoadingStationY * .5 + kOffsetY), kRunSpeed));
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kLeftLoadingStationY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(BackCargoShipToLoadingStation), true));

        List<Waypoint> ForwardCargoShipToLoadingStation = new ArrayList<>();
        ForwardCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX,
                kLeftLoadingStationY + kOffsetY), kRunSpeed));
        ForwardCargoShipToLoadingStation.add(new Waypoint(kLeftLoadingStation, 0));


        //get fingers ready for hatch intake
        ArrayList<Routine> getIntakeReady = new ArrayList<>();
        getIntakeReady.add(new PusherOutRoutine());
        getIntakeReady.add(new FingersRoutine(Fingers.FingersState.CLOSE));

        //drive and ready fingers at the same time
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(ForwardCargoShipToLoadingStation), false),
                new SequentialRoutine(getIntakeReady)));

        //intake hatch
        routines.add(new PusherInRoutine());
        routines.add(new FingersRoutine(Fingers.FingersState.OPEN));

        return new SequentialRoutine(routines);
    }

    public Routine placeHatch2() { //loading station to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts backwards at the loading station after loading a hatch. It then goes around the rocket ship over and shoots it a bit. Then, it lines up with the rocket ship far and places the hatch.
         */

        List<Waypoint> backLoadingStationToRocketShip = new ArrayList<>(); //robot starts going backwards
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftLoadingStationX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kLeftLoadingStationY + kOffsetY), 180)); //backs up a bit
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipMidX + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.8) + kOffsetY), kRunSpeed)); //goes around the rocket ship
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.9 + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.9) + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(backLoadingStationToRocketShip), true)); //robot turns and then moves forward to the rocket ship

        List<Waypoint> forwardLoadingStationToRocketShip = new ArrayList<>(); //robot turns and then moves forward
        forwardLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.6 + kOffsetX,
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) + PhysicalConstants.kRobotLengthInches * .3 + kOffsetY), kRunSpeed)); //line up with rocket ship far
        forwardLoadingStationToRocketShip.add(new Waypoint(kLeftRocketShipFar, 0)); //ends in front of the rocket ship far

        //moves elevator up while driving
        routines.add(new DrivePathRoutine(new Path(forwardLoadingStationToRocketShip), false));

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
                findLineFar(kLeftRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) - PhysicalConstants.kRobotLengthInches * 0.5 + kOffsetY), kRunSpeed));
        RocketShipToDepot.add(new Waypoint(new Translation2d(kCargoShipLeftFrontX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kLeftDepotY - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), kRunSpeed));
        RocketShipToDepot.add(new Waypoint(kLeftDepot, 0));

        //moves elevator down while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(RocketShipToDepot), true),
                new ElevatorCustomPositioningRoutine(0, 1)));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargoClose() { //depot to close rocket ship - shoot cargo into the far rocket ship
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts at the depot after loading a cargo. It then goes to rocket ship close and shoots a cargo into rocket ship far.
         */

        List<Waypoint> DepotToRocketShip = new ArrayList<>();
        DepotToRocketShip.add(new Waypoint(new Translation2d(kLeftRocketShipCloseX * .8 + kOffsetX,
                findLineClose(kLeftRocketShipCloseX * .8) + kOffsetY), kRunSpeed));
        DepotToRocketShip.add(new Waypoint(kLeftRocketShipClose, 0));
        //move elevator up while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToRocketShip), false),
                new ElevatorCustomPositioningRoutine(Configs.get(ElevatorConfig.class).elevatorHeight1, 1)));

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
        return sAlliance.toString();
    }
}