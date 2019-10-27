package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.*;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.fingers.FingersRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherOutRoutine;
import com.palyrobotics.frc2019.config.constants.PhysicalConstants;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")

public class Hecker extends AutoModeBase {
    //right start > rocket ship close > loading station > rocket ship far > depot > rocket ship mid

    public static int kRunSpeed = 90;
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = sDistances.level1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(sDistances.fieldWidth * .5 - (sDistances.cargoRightY + sDistances.cargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kRightLoadingStationX = 0;
    public static double kRightLoadingStationY = -(sDistances.fieldWidth * .5 - sDistances.rightLoadingY);
    public static double kRightDepotX = PhysicalConstants.kUpperPlatformLength;
    public static double kRightDepotY = -(sDistances.fieldWidth * .5 - sDistances.depotFromRightY);
    public static double kRightRocketShipCloseX = sDistances.habRightRocketCloseX + kHabLineX;
    public static double kRightRocketShipCloseY = -(sDistances.fieldWidth * .5 - sDistances.rightRocketCloseY);
    public static double kRightRocketShipMidX = kHabLineX + sDistances.habRightRocketMidX;
    public static double kRightRocketShipMidY = -(sDistances.fieldWidth * .5 - sDistances.rightRocketMidY);
    public static double kRightRocketShipFarX = sDistances.fieldWidth - sDistances.midLineRightRocketFarX;
    public static double kRightRocketShipFarY = -(sDistances.fieldWidth * .5 - sDistances.rightRocketFarY);

    public Translation2d kCargoShipRightFront = new Translation2d(kCargoShipRightFrontX + PhysicalConstants.kRobotWidthInches * .2 + kOffsetX,
            kCargoShipRightFrontY + PhysicalConstants.kRobotLengthInches * .05 + kOffsetY);
    public Translation2d kRightLoadingStation = new Translation2d(kRightLoadingStationX - PhysicalConstants.kRobotLengthInches * 0.5 + kOffsetX,
            kRightLoadingStationY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY);
    public Translation2d kRightRocketShipFar = new Translation2d(kRightRocketShipFarX + PhysicalConstants.kRobotLengthInches * 1 + kOffsetX,
            kRightRocketShipFarY + PhysicalConstants.kRobotLengthInches * .05 + kOffsetY);
    public Translation2d kRightDepot = new Translation2d(kRightDepotX + PhysicalConstants.kRobotLengthInches * 1.1 + kOffsetX,
            kRightDepotY + PhysicalConstants.kRobotLengthInches * .25 + kOffsetY);
    public Translation2d kRightRocketShipClose = new Translation2d(kRightRocketShipCloseX - PhysicalConstants.kRobotLengthInches * 0 + kOffsetX,
            kRightRocketShipCloseY + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY);
    public Translation2d kRightRocketShipMid = new Translation2d(kRightRocketShipMidX + kOffsetX,
            kRightRocketShipMidY + kOffsetY);

    @Override
    public String toString() {
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new ReZeroSubAutoMode().getRoutine(), placeHatchClose1(), takeHatch(), placeHatchClose2());
    }

    public Routine placeHatchClose1() { //start to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> StartToRocketShip = new ArrayList<>();
        StartToRocketShip.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches * 0.5 + kOffsetX,
                0), kRunSpeed)); //goes straight at the start so the robot doesn't get messed up over the ramp
        StartToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipCloseX * .6 + kOffsetX,
                findLineClose(kRightRocketShipCloseX * .8) + PhysicalConstants.kRobotLengthInches * .2 + kOffsetY), kRunSpeed, "visionStart")); //line up with rocket ship
        StartToRocketShip.add(new Waypoint(kRightRocketShipClose, 0));

        routines.add(new VisionAssistedDrivePathRoutine(StartToRocketShip,
                false, false, "visionStart"));

        routines.add(new PusherOutRoutine());

        //release hatch
        routines.add(new FingersRoutine(Fingers.FingersState.CLOSE));
        routines.add(new FingersExpelRoutine(.05));

        routines.add(new TimeoutRoutine(0.4));

        //pusher back in
        routines.add(new PusherInRoutine());

        return new SequentialRoutine(routines);
    }

    public Routine takeHatch() { //rocket ship close to loading station
        ArrayList<Routine> routines = new ArrayList<>();

        ArrayList<Waypoint> BackRocketShipToLoadingStation = new ArrayList<>();
        BackRocketShipToLoadingStation.add(new Waypoint(new Translation2d(-10,
                6), kRunSpeed, true));
        BackRocketShipToLoadingStation.add(new Waypoint(new Translation2d(-40,
                22), 0, true));

//        BackRocketShipToLoadingStation.add(new Waypoint(kRightRocketShipClose.translateBy(new Translation2d(-10,
//                6)), kRunSpeed));
//        BackRocketShipToLoadingStation.add(new Waypoint(kRightRocketShipClose.translateBy(new Translation2d(-40,
//                22)), 0));

        routines.add(new DrivePathRoutine(BackRocketShipToLoadingStation, true, true));

        //turn to face the loading station
        routines.add(new BBTurnAngleRoutineRequireVision(-120));

        ArrayList<Waypoint> ForwardRocketShipToLoadingStation = new ArrayList<>();
        ForwardRocketShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX + PhysicalConstants.kRobotLengthInches,
                kRightLoadingStationY + kOffsetY), kRunSpeed));
        ForwardRocketShipToLoadingStation.add(new Waypoint(new Translation2d(kHabLineX - PhysicalConstants.kRobotLengthInches,
                kRightLoadingStationY + kOffsetY), kRunSpeed, "visionStart"));
        ForwardRocketShipToLoadingStation.add(new Waypoint(kRightLoadingStation, kRunSpeed));

        routines.add(new VisionAssistedDrivePathRoutine(ForwardRocketShipToLoadingStation,
                false, false, "visionStart"));

        routines.add(new PusherOutRoutine());
        routines.add(new FingersRoutine(Fingers.FingersState.OPEN));
        routines.add(new TimeoutRoutine(.5));

        routines.add(new DriveSensorResetRoutine(1));

        return new SequentialRoutine(routines);
    }

    public Routine placeHatchClose2() { //loading station to rocket ship close
        ArrayList<Routine> routines = new ArrayList<>();

        /*
        The robot starts backwards at the loading station after loading a hatch. It then goes around the rocket ship and over shoots it a bit. Then, it lines up with the rocket ship far and places the hatch.
         */
        double newOffset = -kRightLoadingStationY;

        ArrayList<Waypoint> BackLoadingStationToCargoShip = new ArrayList<>();
        BackLoadingStationToCargoShip.add(new Waypoint((new Translation2d(PhysicalConstants.kRobotLengthInches,
                0)), kRunSpeed));
        BackLoadingStationToCargoShip.add(new Waypoint(new Translation2d(kRightRocketShipCloseX * 0.6,
                findLineClose(kRightRocketShipCloseX * 0.6) + PhysicalConstants.kRobotLengthInches * .1 + newOffset), 0));

        routines.add(new DrivePathRoutine(new Path(BackLoadingStationToCargoShip), true));

        //turn to face the loading station
        routines.add(new BBTurnAngleRoutine(110));

        ArrayList<Waypoint> ForwardLoadingStationToRocketShip = new ArrayList<>();
        ForwardLoadingStationToRocketShip.add(new Waypoint(new Translation2d(kRightRocketShipCloseX * 0.65,
                findLineClose(kRightRocketShipCloseX * 0.65) + PhysicalConstants.kRobotLengthInches * .1 + newOffset), kRunSpeed, "startVision"));
        ForwardLoadingStationToRocketShip.add(new Waypoint(kRightRocketShipClose, 0));

        routines.add(new VisionAssistedDrivePathRoutine(ForwardLoadingStationToRocketShip,
                false, false, "startVision"));

        //release hatch
//        routines.add(new FingersRoutine(Fingers.FingersState.OPEN));

        return new SequentialRoutine(routines);
    }

    public double findLineClose(double cordX) {
        return -0.54862 * cordX + 0.54862 * kRightRocketShipCloseX + kRightRocketShipCloseY; //slope is derived from the angle of the rocket ship sides - constants derived from math
    } //the y cord of an invisible line extending from rocket ship close

    public double findLineFar(double cordX) {
        return 0.54862 * cordX - 0.54862 * kRightRocketShipFarX + kRightRocketShipFarY; //slope is derived from the angle of the rocket ship sides - constants derived from math
    } //the y cord of an invisible line extending from rocket ship close

    @Override
    public String getKey() {
        return sAlliance.toString();
    }
}