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

import java.util.ArrayList;
import java.util.List;

public class SpedPoints extends AutoModeBase { //right start > cargo ship front > ball in first cargo > depot x2

//    TODO: finish tuning the code

    public static int SPEED = 150;
    public static double kOffsetX = -Constants.kLowerPlatformLength - Constants.kRobotLengthInches;
    public static double kOffsetY = Constants.kLevel3Width * .5 + Constants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + Constants.kLowerPlatformLength + Constants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
    public static double kHabLineX = Constants.kUpperPlatformLength + Constants.kLowerPlatformLength;
    public static double kRightDepotX = Constants.kUpperPlatformLength;
    public static double kRightDepotY = -(mDistances.kFieldWidth * .5 - mDistances.kDepotFromRightY);
    public static double kRightFirstCargoShipX = kCargoShipRightFrontX + mDistances.kCargoOffsetY;
    public static double kRightFirstCargoShipY = -(mDistances.kFieldWidth * .5 - mDistances.kCargoRightY);
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
        return new SequentialRoutine(new RightStartRightFrontCargo().getRoutine(), ShipToDepot(), placeCargo(0), takeCargo(1), placeCargo(1));
    }

    public Routine ShipToDepot() { //cargo ship to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - Constants.kRobotLengthInches + kOffsetX, kCargoShipRightFrontY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + Constants.kRobotLengthInches * 1.05 + kOffsetX, kRightDepotY + Constants.kRobotLengthInches * .25 + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + Constants.kRobotLengthInches * 1.1 + kOffsetX, kRightDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) { //depot to cargo ship bays
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>(); //the CargoSlot variable makes the robot to a different bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightDepotX + Constants.kRobotLengthInches * 2 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + Constants.kRobotLengthInches * .55 + CargoSlot * Constants.kCargoLineGap + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + Constants.kRobotLengthInches * .85 + CargoSlot * Constants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches + kOffsetY), SPEED)); //line up in front of cargo bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + Constants.kRobotLengthInches * .85 + CargoSlot * Constants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches * .2 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

//        TODO: add ReleaseCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) { //cargo ship bays to de[ot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>(); //the DepotSlot variable makes the robot to a farther to collect the next ball
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * Constants.kCargoLineGap + Constants.kRobotLengthInches + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches * .7 + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * Constants.kCargoLineGap + Constants.kRobotLengthInches + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * Constants.kCargoLineGap - Constants.kRobotLengthInches * .5 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + Constants.kRobotLengthInches - (DepotSlot + 1) * kCargoDiameter + kOffsetX, kRightDepotY + kOffsetY), 0));
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