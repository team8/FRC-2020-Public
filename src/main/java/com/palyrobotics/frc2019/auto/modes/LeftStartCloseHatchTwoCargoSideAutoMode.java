package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoElevator;
import com.palyrobotics.frc2019.config.constants.PhysicalConstants;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class LeftStartCloseHatchTwoCargoSideAutoMode extends AutoModeBase {
    //Left start > cargo ship front > ball in first cargo > depot x2
    //TODO: copy right side version

    public static int kRunSpeed = 150;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipLeftFrontX = sDistances.level1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipLeftFrontY = sDistances.fieldWidth * .5 - (sDistances.cargoLeftY + sDistances.cargoOffsetY);
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kLeftDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kLeftDepotY = sDistances.fieldWidth * .5 - sDistances.depotFromLeftY;
    public static double kLeftFirstCargoShipX = kCargoShipLeftFrontX + sDistances.cargoOffsetY;
    public static double kLeftFirstCargoShipY = sDistances.fieldWidth * .5 - sDistances.cargoLeftY;
    public static double kCargoDiameter = 13;

    @Override
    public String toString() {
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new LeftStartLeftFrontCargoAutoMode().placeHatch(), CargoShipToDepot(), placeCargo(0), takeCargo(1), placeCargo(1));
    }

    public Routine CargoShipToDepot() { //cargo ship to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kCargoShipLeftFrontX - PhysicalConstants.kRobotLengthInches + kOffsetX,
                kCargoShipLeftFrontY + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1.05 + kOffsetX,
                kLeftDepotY - PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), kRunSpeed)); //line up with depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
                kLeftDepotY + kOffsetY), 0));
        //move elevator down while driving
//        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToDepot), true),
//                new ElevatorCustomPositioningRoutine(ElevatorConstants.kBotomPositionInches, 1)));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) { //depot to cargo ship bays
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>(); //the CargoSlot variable makes the robot go farther so it goes to a different bay each time
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kLeftDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .55 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX,
                kLeftDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX,
                kLeftFirstCargoShipY + PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed)); //line up in front of cargo bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX,
                kLeftFirstCargoShipY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));

        //TODO: change path so the robot doesn't go all the way into the cargo bay but instead shoots the cargo in

        //move elevator up while driving
//        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToCargoShip), false),
//                new ElevatorCustomPositioningRoutine(ElevatorConstants.kElevatorCargoBaysHeightInches, 1)));

        //shoot cargo
        routines.add(new PusherOutRoutine());
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 1));

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) { //cargo ship bays to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>(); //the DepotSlot variable makes the robot go farther each time to collect the next cargo
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kLeftFirstCargoShipY + PhysicalConstants.kRobotLengthInches * .7 + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kLeftDepotY + kOffsetY), kRunSpeed)); //turn back and line up with the depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kLeftDepotY + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX,
                kLeftDepotY + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches - (DepotSlot + 1) * kCargoDiameter + kOffsetX,
                kLeftDepotY + kOffsetY), 0));

        //move elevator down while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToDepot), true),
                new ElevatorCustomPositioningRoutine(0, 1)));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return sAlliance.toString();
    }
}