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

@SuppressWarnings("Duplicates")

public class CenterStartRightFrontCargo extends AutoModeBase {

//        TODO: make super accurate (can only be at most 2 inches off) and tune to be faster

    public static int SPEED = 50; //speed can be faster
    public static double kOffsetX = -Constants.kLowerPlatformLength;
    public static double kOffsetY = 0; //starts at center so the offset is 0
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + Constants.kLowerPlatformLength + Constants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
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
        return new SequentialRoutine(new DriveSensorResetRoutine(2), placeHatch());
    }

    public Routine placeHatch() {
        ArrayList<Routine> routines = new ArrayList<>();

//        TODO: make super accurate

        List<Path.Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(-(kHabLineX + Constants.kRobotLengthInches + kOffsetX), 0), SPEED)); //go straight so the robot doesn't get messed up going down a level
        StartToCargoShip.add(new Waypoint(new Translation2d(-(kCargoShipRightFrontX * .6 + kOffsetX), -(kCargoShipRightFrontY + kOffsetY)), SPEED)); //lines up with cargo ship
        StartToCargoShip.add(new Waypoint(new Translation2d(-(kCargoShipRightFrontX - Constants.kRobotLengthInches * .6 + kOffsetX), -(kCargoShipRightFrontY + kOffsetY)), 0));
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


