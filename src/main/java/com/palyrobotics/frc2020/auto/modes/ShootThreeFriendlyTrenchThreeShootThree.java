package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class ShootThreeFriendlyTrenchThreeShootThree extends AutoModeBase {
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

        List<Pose2d> friendlytrench = new ArrayList<Pose2d>();
        friendlytrench.add(new Pose2d(200,60, new Rotation2d(0)));
        //pick up ball
        friendlytrench.add(new Pose2d(170,60, new Rotation2d(0)));
        //pick up ball
        friendlytrench.add(new Pose2d(140,60, new Rotation2d(0)));
        //pick up ball


        List<Pose2d> shoot = new ArrayList<Pose2d>();
        shoot.add(new Pose2d(0,0, new Rotation2d(0)));


        //shoot three balls
        routines.add(new DrivePathRoutine(friendlytrench)); //pick up balls from friendly trench
        routines.add(new DrivePathRoutine(shoot)); //go back to shoot
        //shoot three balls

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return "ShootThreeFriendlyTrenchThree";
    }
}
