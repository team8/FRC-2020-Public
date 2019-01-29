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

public class RightStartRightFrontCargo extends AutoModeBase {

    public static int kRunSpeed = 50; //speed can be faster
    public static double kOffsetX = 0;
    public static double kOffsetY = Constants.kLevel3Width * .5 + Constants.kLevel2Width * .5;
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
        return new SequentialRoutine(new DriveSensorResetRoutine(0.2), placeHatch(true));
    }

    public Routine placeHatch(boolean inverted) {
        ArrayList<Routine> routines = new ArrayList<>();

        //invert the cords if the robot starts backwards
        int invertCord = 1;
        if (inverted) {
            invertCord = -1;
        }

        routines.add(new RezeroSubAutoMode().Rezero(inverted)); //rezero

        List<Path.Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(invertCord * (kHabLineX + Constants.kRobotLengthInches + kOffsetX), 0), kRunSpeed)); //go straight so the robot doesn't get messed up going down a level
        StartToCargoShip.add(new Waypoint(new Translation2d(invertCord * (kCargoShipRightFrontX * .6 + kOffsetX), invertCord * (kCargoShipRightFrontY + kOffsetY)), kRunSpeed)); //lines up with cargo ship
        StartToCargoShip.add(new Waypoint(new Translation2d(invertCord * (kCargoShipRightFrontX - Constants.kRobotLengthInches * .6 + kOffsetX), invertCord * (kCargoShipRightFrontY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(StartToCargoShip), true));

//        routines.add(new ReleaseHatchRoutine()); //routine not made yet

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


