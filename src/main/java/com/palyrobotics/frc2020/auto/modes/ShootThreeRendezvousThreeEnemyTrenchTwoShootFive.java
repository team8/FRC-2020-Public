
package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;


public class ShootThreeRendezvousThreeEnemyTrenchTwoShootFive extends AutoModeBase {

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
        enemyTrench.add(new Pose2d(120, -210, new Rotation2d(0)));
        //pick up ball
        enemyTrench.add(new Pose2d(120, -200, new Rotation2d(0)));
        //pick up ball

        List<Pose2d> rendezvous = new ArrayList<Pose2d>();
        rendezvous.add(new Pose2d(120, -90, new Rotation2d(0)));
        //pick up ball
        rendezvous.add(new Pose2d(110, -70, new Rotation2d(0)));
        //pick up ball
        rendezvous.add(new Pose2d(100, -60, new Rotation2d(0)));
        //pick up ball
      
        List<Pose2d> gobacktostart = new ArrayList<Pose2d>();
        gobacktostart.add(new Pose2d(0, 0, new Rotation2d(0)));

        //shoot 3 balls
        routines.add(new DrivePathRoutine(enemyTrench));
        //pick up 2 balls
        routines.add(new DrivePathRoutine(rendezvous));
        routines.add(new DrivePathRoutine(gobacktostart));
        

        return new SequentialRoutine(routines);
    }


    @Override
    public String getKey() {
        return "RendezvousThreeEnemyTwo";
    }
}
