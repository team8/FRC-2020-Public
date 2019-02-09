package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoGroundIntake;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class RightStartCloseHatchTwoCargoSideAutoMode extends AutoModeBase { //right start > cargo ship front > ball in first cargo > depot x2

//    TODO: finish tuning the code

    public static int SPEED = 150;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kRightDepotX = PhysicalConstants.kUpperPlatformLength;
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
        return new SequentialRoutine(new RightStartRightFrontCargo().placeHatch(false), CargoShipToDepot(), placeCargo(0), takeCargo(1), placeCargo(1));
    }

    public Routine CargoShipToDepot() { //cargo ship to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches + kOffsetX, kCargoShipRightFrontY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1.05 + kOffsetX, kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), SPEED)); //line up with depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX, kRightDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

//        TODO: add IntakeCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) { //depot to cargo ship bays
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>(); //the CargoSlot variable makes the robot go farther so it goes to a different bay each time
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .55 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), SPEED)); //line up in front of cargo bay
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .85 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

//        TODO: add ElevatorCustomPositionRoutine() with new elevator constants
        //routines.add(new ElevatorCustomPositioningRoutine(Constants.kElevatorTopBottomDifferenceInches)); //placeholder
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 3));

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) { //cargo ship bays to depot
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>(); //the DepotSlot variable makes the robot go farther each time to collect the next cargo
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * .7 + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap + PhysicalConstants.kRobotLengthInches + kOffsetX, kRightDepotY + kOffsetY), SPEED)); //turn back and line up with the depot
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + DepotSlot * PhysicalConstants.kCargoLineGap - PhysicalConstants.kRobotLengthInches * .5 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches - (DepotSlot + 1) * kCargoDiameter + kOffsetX, kRightDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

//        TODO: use edited WaitForCargoGroundIntake() (not made yet)
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoGroundIntake());

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}