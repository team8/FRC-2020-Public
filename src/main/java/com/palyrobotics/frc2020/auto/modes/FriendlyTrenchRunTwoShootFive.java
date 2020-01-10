package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

public class FriendlyTrenchRunTwoShootFive extends AutoModeBase {
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
        friendlyTrench.add(new Pose2d(170, 60, new Rotation2d(0)));
        friendlyTrench.add(new Pose2d(140, 60, new Rotation2d(0)));

        List<Pose2d> shoot = new ArrayList<Pose2d>();
        shoot.add(new Pose2d(0, 0, new Rotation2d(0)));
        //will have to adjust this to rotate accordingly.

        routines.add(new DrivePathRoutine(friendlyTrench));
        //pick up balls from our trench
        routines.add(new DrivePathRoutine(shoot));
        //shoot ball

        return new SequentialRoutine(routines);
    }


    @Override
    public String getKey() {
        return "OurTrenchGrab2Shoot";
    }

}
