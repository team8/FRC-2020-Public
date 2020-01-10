package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")

public class Shoot3RendezvousGetShoot3Loading extends AutoModeBase {
    @Override
    public String toString() {
        return this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        //start facing the 3 balls from the leftmost ball if facing the balls from op station
        ArrayList<Routine> routines = new ArrayList<Routine>();
        List<Pose2d> rendezvous1 = new ArrayList<Pose2d>();
        rendezvous1.add(new Pose2d(91, 0, new Rotation2d(0)));

        List<Pose2d> rendezvous2 = new ArrayList<Pose2d>();
        rendezvous2.add(new Pose2d(98, 10, new Rotation2d(0)));

        List<Pose2d> shoot = new ArrayList<Pose2d>();
        shoot.add(new Pose2d(98, 10, new Rotation2d(180)));
        //turn here to face the hoop then shoot
        List<Pose2d> goSupply = new ArrayList<>();
        goSupply.add(new Pose2d(328, 10, new Rotation2d(0)));
        routines.add(new DrivePathRoutine(rendezvous1));
        routines.add(new DrivePathRoutine(rendezvous2));
        //get 3 balls from the rendezvous
        routines.add(new DrivePathRoutine(shoot));
        //shoot ball

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return "Shoot3RendezvousGetThenShoot3ThenGoToLoading";
    }
}
