package com.palyrobotics.frc2020.auto.modes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

@SuppressWarnings("Duplicates")

public class RightRendezvousTwoShootFive extends AutoModeBase {

    @Override
    public String toString() {
        return this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(driveForward());
    }


    @Override
    public String getKey() {
        return "LeftRendezvousGrab2Shoot";
    }

    public Routine driveForward() {
        ArrayList<Routine> routines = new ArrayList<Routine>();
        List<Pose2d> rendezvous1 = new ArrayList<Pose2d>();
        //TODO: Update the positions
        rendezvous1.add(new Pose2d(120, -80, new Rotation2d(0)));

        List<Pose2d> rendezvous2 = new ArrayList<Pose2d>();
        rendezvous2.add(new Pose2d(110, -60, new Rotation2d(0)));

        List<Pose2d> shoot = new ArrayList<Pose2d>();
        shoot.add(new Pose2d(0, 0, new Rotation2d(0)));

        routines.add(new DrivePathRoutine(rendezvous1));
        //pick up ball
        routines.add(new DrivePathRoutine(rendezvous2));
        //pick up ball
        routines.add(new DrivePathRoutine(shoot));
        //shoot ball

        return new SequentialRoutine(routines);
    }
}

