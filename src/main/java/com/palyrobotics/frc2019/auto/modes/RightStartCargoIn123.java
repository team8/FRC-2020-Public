package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
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

public class RightStartCargoIn123 extends AutoModeBase { //right start > cargo ship 1 > depot * 3

//    TODO: tune the code - I haven't tested yet

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
        return new SequentialRoutine(new RezeroSubAutoMode().Rezero(false), placeCargoStart(), placeCargo(1), takeCargo(1), placeCargo(2));
    }

    public Routine placeCargoStart() { //start to cargo ship front
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX, 0), SPEED * .5));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX - PhysicalConstants.kRobotLengthInches * 2 + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), SPEED));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .5 + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(StartToCargoShip), false));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(70)); //turn and then shoot ball into the cargo bays

//        TODO: add ElevatorCustomPositionRoutine() with new elevator constants
        //routines.add(new ElevatorCustomPositioningRoutine(Constants.kElevatorTopBottomDifferenceInches)); //placeholder
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 3));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(-60)); //turn back

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 1 + kOffsetX, kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX, kRightDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>();
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .4 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX - PhysicalConstants.kRobotLengthInches * 2 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .5 + CargoSlot * PhysicalConstants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(70));

//        TODO: add ElevatorCustomPositionRoutine() with new elevator constants
        //routines.add(new ElevatorCustomPositioningRoutine(Constants.kElevatorTopBottomDifferenceInches)); //placeholder
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 3));

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) {
        ArrayList<Routine> routines = new ArrayList<>();

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(-60));

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .5 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches - DepotSlot * kCargoDiameter + kOffsetX, kRightDepotY + kOffsetY), 0));
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