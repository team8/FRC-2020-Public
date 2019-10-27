package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.constants.PhysicalConstants;
import com.palyrobotics.frc2019.config.subsystem.ElevatorConfig;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class RightStartThreeCargoAutoMode extends AutoModeBase {
    //right start > cargo ship 1 > depot x 3
    //triplekill
    public static int kRunSpeed = 80;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches * 0.6;
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
    public static double kCargoDiameter = 13;

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
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new ReZeroSubAutoMode().ReZero(false), placeCargoStart(), placeCargo(1), takeCargo(1), placeCargo(2));
    }

    public Routine placeCargoStart() { //start to cargo ship front
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX,
                0), kRunSpeed * .5));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightRocketShipMidX + kOffsetX,
                0), 0));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(45));

        //move elevator up while driving
        //elevator constant is a placeholder
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(StartToCargoShip), true),
                new ElevatorCustomPositioningRoutine(Configs.get(ElevatorConfig.class).elevatorHatchHeight2, 1)));

        //shoot cargo
        routines.add(new PusherOutRoutine());
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 1));

        List<Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * 0.7 + kOffsetX,
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1 + kOffsetX,
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
                kRightDepotY + kOffsetY), 0));
        //move elevator down while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToDepot), true),
                new ElevatorCustomPositioningRoutine(0, 1)));

        List<Waypoint> goBackABit = new ArrayList<>();
        goBackABit.add(new Waypoint(new Translation2d(0, 0), 20, true));
        goBackABit.add(new Waypoint(new Translation2d(-20, 0), 0, true));

        //drive slowly forward and intake hatch
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(goBackABit), true),
                new IntakeBeginCycleRoutine()));

        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX,
                0), kRunSpeed * .5));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + CargoSlot * PhysicalConstants.kCargoLineGap - PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + CargoSlot * PhysicalConstants.kCargoLineGap - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed * 0.5));
        //turn left toward the cargo ship
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + CargoSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetY), 0));

        //move elevator up while driving
        //elevator constant is a placeholder
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(StartToCargoShip), true),
                new ElevatorCustomPositioningRoutine(Configs.get(ElevatorConfig.class).elevatorHatchHeight2, 1)));

        //shoot cargo
        routines.add(new PusherOutRoutine());
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 1));

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * 0.7 + kOffsetX,
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1 + kOffsetX,
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + DepotSlot * kCargoDiameter + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
                kRightDepotY + kOffsetY), 0));
        //move elevator down while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToDepot), true),
                new ElevatorCustomPositioningRoutine(0, 1)));

        List<Waypoint> goBackABit = new ArrayList<>();
        goBackABit.add(new Waypoint(new Translation2d(0, 0), 20, true));
        goBackABit.add(new Waypoint(new Translation2d(-20, 0), 0, true));

        //drive slowly forward and intake hatch
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(goBackABit), true),
                new IntakeBeginCycleRoutine()));

        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return sAlliance.toString();
    }
}