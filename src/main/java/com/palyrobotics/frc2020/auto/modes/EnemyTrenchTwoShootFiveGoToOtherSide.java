package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class EnemyTrenchTwoShootFiveGoToOtherSide extends AutoModeBase {
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
        List<Pose2d> enemyTrench = new ArrayList<Pose2d>();
        enemyTrench.add(new Pose2d(120, -190, new Rotation2d(0)));
        //pick up ball
        enemyTrench.add(new Pose2d(120, -210, new Rotation2d(0)));
        //pick up other ball

        List<Pose2d> shoot = new ArrayList<Pose2d>();
        shoot.add(new Pose2d(0, 0, new Rotation2d(0)));
        shoot.add(new Pose2d(300, 0, new Rotation2d(0)));

        routines.add(new DrivePathRoutine(enemyTrench));

        //pick up balls from enemy trench

        routines.add(new DrivePathRoutine(shoot));

        //shoot ball

        List<Pose2d> otherside = new ArrayList<Pose2d>();
        otherside.add(new Pose2d(300, 0, new Rotation2d()));

        routines.add(new DrivePathRoutine (otherside));

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return "OpposingTrenchGetShootGoOtherSide";
    }
}
