
package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

public class ShootThreeGoToOtherSide extends AutoModeBase {
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

        List<Pose2d> otherside = new ArrayList<Pose2d>();
        otherside.add(new Pose2d(350, 0, new Rotation2d(0)));


        //shoot 3 balls
        //pick up 2 balls
        routines.add(new DrivePathRoutine(otherside));

        return new SequentialRoutine(routines);
    }


    @Override
    public String getKey() {
        return "ShootThreeGoToOtherSide";
    }
}
