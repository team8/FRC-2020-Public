package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class TestAuto extends AutoModeBase {

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new DrivePathRoutine(
                new Pose2d(),
                new Pose2d(5, 0, new Rotation2d())
        );
    }

    @Override
    public String getKey() {
        return null;
    }
}
