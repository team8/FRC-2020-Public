package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.List;

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
        return new SequentialRoutine(test(), test2(), new TimeoutRoutine(1));
    }

    Routine test() {
        return new DrivePathRoutine(List.of(
                new Pose2d(),
                new Pose2d(50, 0, new Rotation2d())));
    }
    Routine test2() {
        return new DrivePathRoutine(List.of(
                new Pose2d(100, 0, new Rotation2d()),
                new Pose2d(500, 0, new Rotation2d())));
    }

    @Override
    public String getKey() {
        return null;
    }
}
