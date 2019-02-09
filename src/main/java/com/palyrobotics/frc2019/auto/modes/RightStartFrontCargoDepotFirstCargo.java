package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.intake.IntakeBeginCycleRoutine;
import com.palyrobotics.frc2019.behavior.routines.pusher.PusherInRoutine;
import com.palyrobotics.frc2019.behavior.routines.shooter.ShooterExpelRoutine;
import com.palyrobotics.frc2019.behavior.routines.waits.WaitForCargoGroundIntake;
import com.palyrobotics.frc2019.config.Constants.PhysicalConstants;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

public class RightStartFrontCargoDepotFirstCargo extends AutoModeBase {

    //TODO: replace with Triplekill because Triplekill is faster and does more

    public static int SPEED = 70; //can be faster
    public static double kOffsetX = -PhysicalConstants.kLowerPlatformLength - PhysicalConstants.kRobotLengthInches;
    public static double kOffsetY = PhysicalConstants.kLevel3Width * .5 + PhysicalConstants.kLevel2Width * .5;
    public static double kCargoShipRightFrontX = mDistances.kLevel1CargoX + PhysicalConstants.kLowerPlatformLength + PhysicalConstants.kUpperPlatformLength;
    public static double kCargoShipRightFrontY = -(mDistances.kFieldWidth * .5 - (mDistances.kCargoRightY + mDistances.kCargoOffsetY));
    public static double kHabLineX = PhysicalConstants.kUpperPlatformLength + PhysicalConstants.kLowerPlatformLength;
    public static double kRightDepotX = PhysicalConstants.kUpperPlatformLength;
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
        forwardRocketShipToDepot.add(new Waypoint(new Translation2d(-(kCargoShipRightFrontX - PhysicalConstants.kRobotWidthInches * .3 + kOffsetX), -(kCargoShipRightFrontY + kOffsetY)), SPEED));
        forwardRocketShipToDepot.add(new Waypoint(new Translation2d(-(kHabLineX + PhysicalConstants.kRobotLengthInches * .5 + kOffsetX), -(kRightDepotY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(forwardRocketShipToDepot), false));

        routines.add(new CascadingGyroEncoderTurnAngleRoutine(120));

        List<Path.Waypoint> backRocketShipToDepot = new ArrayList<>();
        backRocketShipToDepot.add(new Waypoint(new Translation2d(-(kRightDepotX + PhysicalConstants.kRobotLengthInches * 2 + kOffsetX), -(kRightDepotY + kOffsetY)), SPEED));
        backRocketShipToDepot.add(new Waypoint(new Translation2d(-(kRightDepotX + PhysicalConstants.kRobotLengthInches + kOffsetX), -(kRightDepotY + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(backRocketShipToDepot), true));

//        TODO: use edited WaitForCargoGroundIntake() (not made yet)
        routines.add(new IntakeBeginCycleRoutine());
        routines.add(new WaitForCargoGroundIntake());

        return new SequentialRoutine(routines);
    }

    public Routine placeCargo() {
        ArrayList<Routine> routines = new ArrayList<>();

        List<Path.Waypoint> DepotToCargoShip = new ArrayList<>();
        DepotToCargoShip.add(new Waypoint(new Translation2d(-(kRightFirstCargoShipX - PhysicalConstants.kRobotLengthInches + kOffsetX), -(kRightDepotY + kOffsetY)), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(-(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches * .9 + kOffsetX), -(kRightDepotY - PhysicalConstants.kRobotLengthInches * .4 + kOffsetY)), SPEED));
        DepotToCargoShip.add(new Waypoint(new Translation2d(-(kRightFirstCargoShipX + PhysicalConstants.kRobotLengthInches + kOffsetX), -(kRightFirstCargoShipY - PhysicalConstants.kRobotLengthInches * .6 + kOffsetY)), 0));
        routines.add(new DrivePathRoutine(new Path(DepotToCargoShip), false));

//        TODO: add ElevatorCustomPositionRoutine() with new elevator constants
        //routines.add(new ElevatorCustomPositioningRoutine(Constants.kElevatorTopBottomDifferenceInches)); //placeholder
        routines.add(new ShooterExpelRoutine(Shooter.ShooterState.SPIN_UP, 3));

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


