package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.VisionAssistedDrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.config.constants.PhysicalConstants;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")

public class RightStartRightFrontCargoAutoMode extends AutoModeBase {

    public static int kRunSpeed = 90; //speed can be faster
    //    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetX = -20 - 25;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = sDistances.level1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(sDistances.fieldWidth * .5 - (sDistances.cargoRightY + sDistances.cargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;

    @Override
    public String toString() {
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(0.1), placeHatch());
//        return new SequentialRoutine(new RezeroSubAutoMode().getRoutine(), placeHatch());
    }

    public Routine placeHatch() {
        ArrayList<Routine> routines = new ArrayList<>();

        //pusher out
        routines.add(new PusherOutRoutine());

        ArrayList<Waypoint> StartToCargoShip = new ArrayList<>();
        StartToCargoShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 0.1 + kOffsetX,
                kCargoShipRightFrontY * 0.7), 40));

        //lines up with cargo ship
        StartToCargoShip.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .6 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.35 + kOffsetY), kRunSpeed));
        //turn on vision
        StartToCargoShip.add(new Waypoint(new Translation2d((kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7) * .85 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.35 + kOffsetY), kRunSpeed, "visionStart"));
        StartToCargoShip.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7 + kOffsetX,
                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.35 + kOffsetY), 0));

//                //lines up with cargo ship
//        StartToCargoShip.add(new Waypoint(new Translation2d(kCargoShipRightFrontX * .65 + kOffsetX,
//                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.1 + kOffsetY), kRunSpeed));
//        //turn on vision
//        StartToCargoShip.add(new Waypoint(new Translation2d((kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7) * 0.95 + kOffsetX,
//                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.1 + kOffsetY), kRunSpeed, "visionStart"));
//        StartToCargoShip.add(new Waypoint(new Translation2d(kCargoShipRightFrontX - PhysicalConstants.kRobotLengthInches * 0.7 + kOffsetX,
//                kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * 0.2 + kOffsetY), 0));

//        routines.add(new DrivePathRoutine(new Path(StartToCargoShip), false));
//        drivepath + vision
        routines.add(new VisionAssistedDrivePathRoutine(StartToCargoShip, false, false, "visionStart"));

//        routines.add(new VisionClosedDriveRoutine());

        //release hatch
        routines.add(new FingersRoutine(Fingers.FingersState.CLOSE));
        routines.add(new FingersExpelRoutine(0.05));

        //wait
        routines.add(new TimeoutRoutine(0.4));

        //pusher back in
        routines.add(new PusherInRoutine());


        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return sAlliance.toString();
    }
}


