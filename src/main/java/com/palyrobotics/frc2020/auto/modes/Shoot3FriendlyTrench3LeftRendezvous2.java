package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.ParallelDrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.ShootAllBallsRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

public class Shoot3FriendlyTrench3LeftRendezvous2 extends AutoModeBase {

    @SuppressWarnings("Duplicates")

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
        List<Pose2d> friendlyTrench = new ArrayList<Pose2d>();
        friendlyTrench.add(new Pose2d(200, 60, new Rotation2d(0)));
        friendlyTrench.add(new Pose2d(170, 60, new Rotation2d(0)));
        friendlyTrench.add(new Pose2d(140, 60, new Rotation2d(0)));

        List<Pose2d>  rendezvous1= new ArrayList<Pose2d>();
        rendezvous1.add(new Pose2d(140, -10, new Rotation2d(0)));
        //go collect first rendezvous ball
        rendezvous1.add(new Pose2d(120, -20, new Rotation2d(0)));
        //get second ball


        List<Pose2d> shoot = new ArrayList<Pose2d>();
        shoot.add(new Pose2d(0, 0, new Rotation2d(0)));
        //will have to adjust this to rotate accordingly.

        routines.add(new DrivePathRoutine(friendlyTrench));
        //pick up balls from our trench
        routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rendezvous1), 0.8));

        //collect two balls
        routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(shoot), 0.8));
        //shoot ball

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return "Shoot3GoFriendlyTrenchGet3RendezvousGet2";
    }

}
