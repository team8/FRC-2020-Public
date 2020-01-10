package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class FriendlyTrenchRunGetFive extends AutoModeBase {
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

        List<Pose2d> friendlyTrench1 = new ArrayList<Pose2d>();
        friendlyTrench1.add(new Pose2d(140, 60, new Rotation2d(0)));
        friendlyTrench1.add(new Pose2d(170, 60, new Rotation2d(0)));
        friendlyTrench1.add(new Pose2d(200, 60, new Rotation2d(0)));


        List<Pose2d> aroundTrench = new ArrayList<Pose2d>();
        aroundTrench.add(new Pose2d(200, 0, new Rotation2d(0)));
        aroundTrench.add(new Pose2d(300, 0, new Rotation2d(0)));
        aroundTrench.add(new Pose2d(300, 50, new Rotation2d(0)));
        //pick up ball
        aroundTrench.add(new Pose2d(300, 70, new Rotation2d(0)));
        //pick up ball


        routines.add(new DrivePathRoutine(friendlyTrench1)); //gets to friendly trench
      
        //pick up balls, end up where ball closest to spinner is

        routines.add(new DrivePathRoutine(aroundTrench)); //goes around the spinner
        // routines.add(new DrivePathRoutine(friendlyTrench2)); //gets the two balls on the other side of the spinner
    
        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return "FriendlyTrenchRunGetFive";
    }


}
