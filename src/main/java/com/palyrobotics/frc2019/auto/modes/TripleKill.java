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

public class TripleKill extends AutoModeBase { //right start > cargo ship front > loading station > rocket ship far > depot > close rocket ship

//    TODO: tune the code - I haven't tested yet

    public static int SPEED = 150;
    public static double kOffsetX = -Constants.kLowerPlatformLength - Constants.kRobotLengthInches;
    public static double kOffsetY = 0;
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
        return new SequentialRoutine(new DriveSensorResetRoutine(0.2), placeCargoStart(), placeCargo(1), takeCargo(1), placeCargo(2));
    }

    public Routine placeCargoStart() { //start to cargo ship front
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + Constants.kRobotLengthInches + kOffsetX, 0), SPEED * .5));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX - Constants.kRobotLengthInches * 2 + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches + kOffsetY), SPEED));
        StartToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + Constants.kRobotLengthInches * .5 + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(StartToCargoShip), false));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(70)); //turn and then shoot ball into the cargo bays

//        TODO: add ReleaseCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(-70)); //turn back

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + Constants.kRobotLengthInches * 1 + kOffsetX, kRightDepotY + Constants.kRobotLengthInches * .25 + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + Constants.kRobotLengthInches * 1.1 + kOffsetX, kRightDepotY + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(CargoShipToDepot), true));

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo(int CargoSlot) {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>();
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .4 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX - Constants.kRobotLengthInches * 2 + CargoSlot * Constants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches + kOffsetY), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(kRightFirstCargoShipX + Constants.kRobotLengthInches * .5 + CargoSlot * Constants.kCargoLineGap + kOffsetX, kRightFirstCargoShipY - Constants.kRobotLengthInches + kOffsetY), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(70));

//        TODO: add ReleaseCargoRoutine (not made yet)
//        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine takeCargo(int DepotSlot) {
        ArrayList<Routine> routines = new ArrayList<>();

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(-70));

        List<Path.Waypoint> CargoShipToDepot = new ArrayList<>();
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightFirstCargoShipX * .5 + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kHabLineX + Constants.kRobotLengthInches + kOffsetX, kRightDepotY + kOffsetY), SPEED));
        CargoShipToDepot.add(new Waypoint(new Translation2d(kRightDepotX + Constants.kRobotLengthInches - DepotSlot * kCargoDiameter + kOffsetX, kRightDepotY + kOffsetY), 0));
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