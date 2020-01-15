package com.palyrobotics.frc2020.auto.modes;

import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.WaitRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

public class TestAuto extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		return new SequentialRoutine(test(), test2(), new WaitRoutine(1));
	}

	Routine test() {
		return new DrivePathRoutine(List.of(new Pose2d(), new Pose2d(50, 0, new Rotation2d())));
	}

	Routine test2() {
		return new DrivePathRoutine(
				List.of(new Pose2d(100, 0, new Rotation2d()), new Pose2d(500, 0, new Rotation2d())));
	}
}
