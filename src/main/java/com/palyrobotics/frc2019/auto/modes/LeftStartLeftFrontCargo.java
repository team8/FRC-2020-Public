package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class LeftStartLeftFrontCargo extends AutoModeBase {

//        TODO: make super accurate (can only be at most 2 inches off) and tune to be faster

    public static int SPEED = 60; //can be faster
    public static double kOffsetX = Constants.kLowerPlatformLength;
    public static double kOffsetY = -(Constants.kLevel3Width * .5 + Constants.kLevel2Width * .5);
    public static double kCargoShipLeftFrontX = mDistances.kLevel1CargoX + Constants.kLowerPlatformLength + Constants.kUpperPlatformLength;
    public static double kCargoShipLeftFrontY = mDistances.kFieldWidth * .5 - (mDistances.kCargoLeftY + mDistances.kCargoOffsetY);
    public static double kHabLineX = Constants.kUpperPlatformLength + Constants.kLowerPlatformLength;

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(0.2), new Rezero().getRoutine(), placeHatch());
    }

    public Routine placeHatch() {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(-(kHabLineX + Constants.kRobotLengthInches + kOffsetX), 0), SPEED * .75));
        StartToCargoShip.add(new Waypoint(new Translation2d(-(kCargoShipLeftFrontX * .6 + kOffsetX), -(kCargoShipLeftFrontY + kOffsetY)), SPEED));
        StartToCargoShip.add(new Waypoint(new Translation2d(-(kCargoShipLeftFrontX - Constants.kRobotLengthInches * 2 + kOffsetX), -(kCargoShipLeftFrontY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(StartToCargoShip), true));

//        TODO: implement ReleaseHatchRoutine when created
//        routines.add(new ReleaseHatchRoutine()); //routine not made yet

        routines.add(new TimeoutRoutine(1)); //placeholder

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


