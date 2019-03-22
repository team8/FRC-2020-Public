package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersOpenRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Constants.PhysicalConstants;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class RightStartRightFrontCargoAutoMode extends AutoModeBase {

    public static int kRunSpeed = 70; //speed can be faster
    public static double kOffsetX = -PhysicalConstants.kUpperPlatformLength - PhysicalConstants.kRobotLengthInches * 0.6;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
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
        return new SequentialRoutine(new DriveSensorResetRoutine(0.2), placeHatch());
    }

    public Routine placeHatch() {
        ArrayList<Routine> routines = new ArrayList<>();

        //rezero
        //routines.add(new RezeroSubAutoMode().Rezero(false));

        ArrayList<Waypoint> StartToCargoShip = new ArrayList<>();
//        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches + kOffsetX,
//                0), kRunSpeed)); //go straight so the robot doesn't get messed up going down a level
        StartToCargoShip.add(new Waypoint(new Translation2d((kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7) * .8 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetY), kRunSpeed)); //lines up with cargo ship
        StartToCargoShip.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetY), 0));

        //move elevator up while driving
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(StartToCargoShip), false),
                new ElevatorCustomPositioningRoutine(OtherConstants.kCargoHatchTargetHeight, 1)));

        ArrayList<Waypoint> goForward = new ArrayList<>();
        goForward.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetY), 20));
        goForward.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.4 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetY), 0));

        //pusher out while driving forward slowly
        routines.add(new ParallelRoutine(new DrivePathRoutine(new Path(goForward), false), new PusherOutRoutine()));

        //release hatch
//        routines.add(new FingersOpenRoutine());

        //pusher back in
        routines.add(new PusherInRoutine());

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


