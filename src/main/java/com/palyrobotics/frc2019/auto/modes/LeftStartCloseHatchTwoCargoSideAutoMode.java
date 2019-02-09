package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class LeftStartCloseHatchTwoCargoSideAutoMode extends AutoModeBase {
    //Left start > cargo ship front > ball in first cargo > depot x2

    public static int kRunSpeed = 150;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipLeftFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipLeftFrontY = mDistances.kFieldWidth * .5 - (mDistances.kCargoLeftY + mDistances.kCargoOffsetY);
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kLeftDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kLeftDepotY = mDistances.kFieldWidth * .5 - mDistances.kDepotFromLeftY;
    public static double kLeftFirstCargoShipX = kCargoShipLeftFrontX + mDistances.kCargoOffsetY;
    public static double kLeftFirstCargoShipY = mDistances.kFieldWidth * .5 - mDistances.kCargoLeftY;
    public static double kCargoDiameter = 13;

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new LeftStartLeftFrontCargo().placeHatch(false), CargoShipToDepot(), placeCargo(0), takeCargo(1), placeCargo(1));
    }

    public Routine CargoShipToDepot() { //cargo ship to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kCargoShipLeftFrontX - PhysicalConstants.kRobotLengthInches + kOffsetX, kCargoShipLeftFrontY + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1.05 + kOffsetX, kLeftDepotY - PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), kRunSpeed)); //line up with depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX, kLeftDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

//        TODO: add IntakeCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) { //depot to cargo ship bays
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>(); //the CargoSlot variable makes the robot go farther so it goes to a different bay each time
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX, kLeftDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .55 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kLeftDepotY + kOffsetY), kRunSpeed));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kLeftFirstCargoShipY + PhysicalConstants.kRobotLengthInches + kOffsetY), kRunSpeed)); //line up in front of cargo bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kLeftFirstCargoShipY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

//        TODO: add ReleaseCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) { //cargo ship bays to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>(); //the DepotSlot variable makes the robot go farther each time to collect the next cargo
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX, kLeftFirstCargoShipY + PhysicalConstants.kRobotLengthInches * .7 + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX, kLeftDepotY + kOffsetY), kRunSpeed)); //turn back and line up with the depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX, kLeftDepotY + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX, kLeftDepotY + kOffsetY), kRunSpeed));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kLeftDepotX + PhysicalConstants.kRobotLengthInches - (DepotSlot + 1) * kCargoDiameter + kOffsetX, kLeftDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

//        TODO: add IntakeCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}