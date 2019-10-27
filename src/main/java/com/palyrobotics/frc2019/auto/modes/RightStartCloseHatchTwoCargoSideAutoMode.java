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
import com.palyrobotics.frc2019.config.subsystem.ElevatorConfig;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class RightStartCloseHatchTwoCargoSideAutoMode extends AutoModeBase {
    //right start > cargo ship front > ball in first cargo > depot x2
    //TODO: make work (not done)

    public static int SPEED = 150;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = sDistances.level1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(sDistances.fieldWidth * .5 - (sDistances.cargoRightY + sDistances.cargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kRightDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kRightDepotY = -(sDistances.fieldWidth * .5 - sDistances.depotFromRightY);
    public static double kRightFirstCargoShipX = kCargoShipRightFrontX + sDistances.cargoOffsetY;
    public static double kRightFirstCargoShipY = -(sDistances.fieldWidth * .5 - sDistances.cargoRightY);
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
        return new SequentialRoutine(new RightStartRightFrontCargoAutoMode().placeHatch(), CargoShipToDepot(), placeCargo(0), takeCargo(1), placeCargo(1));
    }

    public Routine CargoShipToDepot() { //cargo ship to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches + kOffsetX,
                kCargoShipRightFrontY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1.05 + kOffsetX,
                kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), SPEED)); //line up with depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
                kRightDepotY + kOffsetY), 0));
        //move elevator down
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToDepot), true),
                new ElevatorCustomPositioningRoutine(0, 1)));

        //intake cargo
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoElevator());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) { //depot to cargo ship bays
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>(); //the CargoSlot variable makes the robot go farther so it goes to a different bay each time
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX,
                kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .55 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX,
                kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), SPEED)); //line up in front of cargo bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));

        //move elevator up while driving
        //elevator constant is a placeholder
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(DepotToCargoShip), false),
                new ElevatorCustomPositioningRoutine(Configs.get(ElevatorConfig.class).elevatorHatchHeight2, 1)));
        //change elevator constant

        //shoot cargo
        routines.add(new PusherOutRoutine());
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 1));

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) { //cargo ship bays to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>(); //the DepotSlot variable makes the robot go farther each time to collect the next cargo
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * .7 + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX,
                kRightDepotY + kOffsetY), SPEED)); //turn back and line up with the depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX,
                kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches - (DepotSlot + 1) * kCargoDiameter + kOffsetX,
                kRightDepotY + kOffsetY), 0));
        //move elevator down while driving
        //elevator constant is a placeholder
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(CargoShipToDepot), true),
                new ElevatorCustomPositioningRoutine(Configs.get(ElevatorConfig.class).elevatorHatchHeight2, 1)));
        //change elevator constant

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