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

public class RightStartFrontCargoDepotFirstCargo extends AutoModeBase {

    //TODO: replace with Triplekill because Triplekill is faster and does more

    public static int SPEED = 70; //can be faster
    public static double kOffsetX = -Constants.kLowerPlatformLength - Constants.kRobotLengthInches;
    public static double kOffsetY = Constants.kLevel3Width * .5 + Constants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + Constants.kLowerPlatformLength + Constants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
    public static double kHabLineX = Constants.kUpperPlatformLength + Constants.kLowerPlatformLength;
    public static double kRightDepotX = Constants.kUpperPlatformLength;
    public static double kRightDepotY = -(mDistances.kFieldWidth * .5 - mDistances.kDepotFromRightY);
    public static double kRightFirstCargoShipX = kCargoShipRightFrontX + mDistances.kCargoOffsetY;
    public static double kRightFirstCargoShipY = -(mDistances.kFieldWidth * .5 - mDistances.kCargoRightY);

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(0.2), new RightStartRightFrontCargo().getRoutine(), takeCargo(), placeCargo());
    }

    public Routine takeCargo() {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> forwardRocketShipToDepot = new ArrayList<>();
        forwardRocketShipToDepot.add(new Waypoint(new Translation2d(-(kCargoShipRightFrontX - Constants.kRobotWidthInches * .3 + kOffsetX), -(kCargoShipRightFrontY + kOffsetY)), SPEED));
        forwardRocketShipToDepot.add(new Waypoint(new Translation2d(-(kHabLineX + Constants.kRobotLengthInches * .5 + kOffsetX), -(kRightDepotY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(forwardRocketShipToDepot), false));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(120));

        List<Path.Waypoint> backRocketShipToDepot = new ArrayList<>();
        backRocketShipToDepot.add(new Waypoint(new Translation2d(-(kRightDepotX + Constants.kRobotLengthInches * 2 + kOffsetX), -(kRightDepotY + kOffsetY)), SPEED));
        backRocketShipToDepot.add(new Waypoint(new Translation2d(-(kRightDepotX + Constants.kRobotLengthInches + kOffsetX), -(kRightDepotY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(backRocketShipToDepot), true));

//        TODO: implement IntakeCargoRoutine when created
//        routines.add(new IntakeCargoRoutine()); //routine not made yet
        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo() {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>();
        DepotToCargoShip.add(new Waypoint(new Translation2d(-(kRightFirstCargoShipX - Constants.kRobotLengthInches + kOffsetX), -(kRightDepotY + kOffsetY)), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(-(kRightFirstCargoShipX + Constants.kRobotLengthInches * .9 + kOffsetX), -(kRightDepotY - Constants.kRobotLengthInches * .4 + kOffsetY)), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(-(kRightFirstCargoShipX + Constants.kRobotLengthInches + kOffsetX), -(kRightFirstCargoShipY - Constants.kRobotLengthInches * .6 + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

//        TODO: implement ReleaseCargoRoutine when created
//        routines.add(new ReleaseCargoRoutine()); //routine not made yet
        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


