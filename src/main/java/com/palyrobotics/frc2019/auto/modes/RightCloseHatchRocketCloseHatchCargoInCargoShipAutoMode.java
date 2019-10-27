package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.constants.OtherConstants;
import com.palyrobotics.frc2019.config.constants.PhysicalConstants;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class RightCloseHatchRocketCloseHatchCargoInCargoShipAutoMode extends AutoModeBase {
    //right start > cargo ship front > loading station > rocket ship far > depot > rocket ship close (fullsend)
    //TODO: make work (not done)

    public static int kRunSpeed = 120;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = sDistances.level1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(sDistances.fieldWidth * .5 - (sDistances.cargoRightY + sDistances.cargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kRightLoadingStationX = 0;
    public static double kRightLoadingStationY = -(sDistances.fieldWidth * .5 - sDistances.rightLoadingY);
    public static double kRightDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kRightDepotY = -(sDistances.fieldWidth * .5 - sDistances.depotFromRightY);
    public static double kRightRocketShipCloseX = sDistances.habRightRocketCloseX + kHabLineX;
    public static double kRightRocketShipCloseY = -(sDistances.fieldWidth * .5 - sDistances.rightRocketCloseY);
    public static double kRightRocketShipMidX = kHabLineX + sDistances.habRightRocketMidX;
    public static double kRightRocketShipMidY = -(sDistances.fieldWidth * .5 - sDistances.rightRocketMidY);
    public static double kRightRocketShipFarX = sDistances.fieldWidth - sDistances.midLineRightRocketFarX;
    public static double kRightRocketShipFarY = -(sDistances.fieldWidth * .5 - sDistances.rightRocketFarY);
    public static double kRightFirstCargoShipX = kCargoShipRightFrontX + sDistances.cargoOffsetY;
    public static double kRightFirstCargoShipY = -(sDistances.fieldWidth * .5 - sDistances.cargoRightY);

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

    @Override
    public String toString() {
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new RightStartRightFrontCargoAutoMode().placeHatch(), takeHatch(), placeHatch2(), takeCargo(), placeCargo());
    }

    public Routine takeHatch() { //cargo ship front to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> BackCargoShipToLoadingStation = new ArrayList<>();
        BackCargoShipToLoadingStation.add(new Waypoint(kCargoShipRightFront.translateBy
                (new Translation2d(-PhysicalConstants.kRobotLengthInches, 0)), kRunSpeed)); //backs out of the cargo ship
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .5 + kOffsetX,
                kRightLoadingStationY * .5 + kOffsetY), kRunSpeed));
        BackCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .5 - PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightLoadingStationY + kOffsetY), 0));
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(BackCargoShipToLoadingStation), true),
                new ElevatorCustomPositioningRoutine(OtherConstants.kLoadingHatchTargetHeight, 1)));

        //turn toward the loading station
        routines.add(new CascadingGyroEncoderTurnAngleRoutine(90));

        List<Waypoint> ForwardCargoShipToLoadingStation = new ArrayList<>();
        ForwardCargoShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kRightLoadingStationY + PhysicalConstants.kRobotWidthInches * .4 + kOffsetY), kRunSpeed));
        ForwardCargoShipToLoadingStation.add(new Waypoint(kRightLoadingStation, 0));

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

    public Routine placeHatch2() { //loading station to rocket ship far
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts backwards at the loading station after loading a hatch. It then goes around the rocket ship over and shoots it a bit. Then, it lines up with the rocket ship far and places the hatch.
         */

        List<Waypoint> backLoadingStationToRocketShip = new ArrayList<>(); //robot starts going backwards
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightLoadingStationX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightLoadingStationY + kOffsetY), 180)); //backs up a bit
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipMidX + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.8) + kOffsetY), kRunSpeed)); //goes around the rocket ship
        backLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.9 + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.9) - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(backLoadingStationToRocketShip), true)); //robot turns and then moves forward to the rocket ship

        List<Waypoint> forwardLoadingStationToRocketShip = new ArrayList<>(); //robot turns and then moves forward
        forwardLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1.6 + kOffsetX,
                findLineFar(kRightRocketShipMidX + PhysicalConstants.kRobotLengthInches * 1.6) - PhysicalConstants.kRobotLengthInches * .3 + kOffsetY), kRunSpeed)); //line up with rocket ship far
        forwardLoadingStationToRocketShip.add(new Waypoint(kRightRocketShipFar, 0)); //ends in front of the rocket ship far

        //move elevator up while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(forwardLoadingStationToRocketShip), false),
                new ElevatorCustomPositioningRoutine(OtherConstants.kRocketHatchTargetHeight, 1)));

        //place hatch
        routines.add(new FingersCycleRoutine(1));


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
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), kRunSpeed));
        RocketShipToDepot.add(new Waypoint(kRightDepot, 0));
        //move elevator down while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(RocketShipToDepot), true),
                new ElevatorCustomPositioningRoutine(0, 1)));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());


        return new SequentialRoutine(routines);
    }

    public Routine placeCargo() { //depot to close rocket ship - shoot cargo into the far rocket ship
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts at the depot after loading a cargo. It then goes to first cargo ship bay and throws a cargo in
         */

        List<Waypoint> DepotToCargoShip = new ArrayList<>();
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .4 + kOffsetX,
                kRightDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX - PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), 0));
        //move elevator up while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToCargoShip), true),
                new ElevatorCustomPositioningRoutine(0, 1)));
        //change elevator constant

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(70));

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
        return sAlliance.toString();
    }
}