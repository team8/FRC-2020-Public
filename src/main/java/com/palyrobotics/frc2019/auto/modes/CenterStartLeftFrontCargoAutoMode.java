package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCloseRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersExpelRoutine;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class CenterStartLeftFrontCargoAutoMode extends AutoModeBase {

    public static int kRunSpeed = 50;

    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength;
    public static double kOffsetY = 0; //starts at center so the offset is 0
    public static double kCargoShipLeftFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipLeftFrontY = mDistances.kFieldWidth * .5 - (mDistances.kCargoLeftY + mDistances.kCargoOffsetY);
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(placeHatch(true));
    }

    public Routine placeHatch(boolean inverted) {
        ArrayList<Routine> routines = new ArrayList<>();

        //invert the cords if the robot starts backwards
        int invetCord = 1;
        if (inverted) {
            invetCord = -1;
        }

        routines.add(new RezeroSubAutoMode().Rezero(inverted)); //rezero

        List<Path.Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(invetCord * (kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX), 0), kRunSpeed)); //go straight so the robot doesn't get messed up going down a level
        StartToCargoShip.add(new Waypoint(new Translation2d(invetCord * (kCargoShipLeftFrontX * .6 + kOffsetX), invetCord * (kCargoShipLeftFrontY + kOffsetY)), kRunSpeed)); //lines up with cargo ship
        StartToCargoShip.add(new Waypoint(new Translation2d(invetCord * (kCargoShipLeftFrontX - PhysicalConstants.kRobotLengthInches * .6 + kOffsetX), invetCord * (kCargoShipLeftFrontY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(StartToCargoShip), true));

        routines.add(new FingersCloseRoutine());
        routines.add(new FingersExpelRoutine(3));
        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


