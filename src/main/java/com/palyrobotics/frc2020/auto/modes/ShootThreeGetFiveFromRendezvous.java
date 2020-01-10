package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

import com.palyrobotics.frc2020.behavior.routines.drive.ParallelDrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.ShootAllBallsRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class ShootThreeGetFiveFromRendezvous extends AutoModeBase {

    @Override
    public String toString() {
        return this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        ArrayList<Routine> routines = new ArrayList<Routine>();
       
        //shoot 3 balls

        List<Pose2d> leftrendezvous1 = new ArrayList<Pose2d>();
        leftrendezvous1.add(new Pose2d(140, -20, new Rotation2d(0)));

        List<Pose2d> leftrendezvous2 = new ArrayList<Pose2d>();
        leftrendezvous2.add(new Pose2d(130, -25, new Rotation2d(0)));

        List<Pose2d> rightrendezvous1 = new ArrayList<Pose2d>();
        rightrendezvous1.add(new Pose2d(115,-50, new Rotation2d(0)));

        List<Pose2d> rightrendezvous11 = new ArrayList<Pose2d>();
        rightrendezvous11.add(new Pose2d(110, -60, new Rotation2d(0)));

        List<Pose2d> rightrendezvous2 = new ArrayList<Pose2d>();
        rightrendezvous2.add(new Pose2d(120,-70, new Rotation2d(0)));

        List<Pose2d> rightrendezvous3 = new ArrayList<Pose2d>();
        rightrendezvous3.add(new Pose2d(125,-90, new Rotation2d(0)));

        List<Pose2d> backtostart = new ArrayList<Pose2d>();
        rightrendezvous3.add(new Pose2d(0,0, new Rotation2d(0)));

        //todo: check if rendezvous are where the balls are picked up and comments to be added.

        routines.add(new ShootAllBallsRoutine());
        //shoot

        routines.add(new DrivePathRoutine(leftrendezvous1));
        routines.add(new DrivePathRoutine(leftrendezvous2));
        routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(leftrendezvous2), 0.8));
        routines.add(new DrivePathRoutine(rightrendezvous1));
        routines.add(new DrivePathRoutine(rightrendezvous2));
        routines.add(new DrivePathRoutine(rightrendezvous3));
        routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rightrendezvous3), 0.8));

        routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(backtostart), 0.8));



        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return "ShootGetFromRendezvous";
    }


}
