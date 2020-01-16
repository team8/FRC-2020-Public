package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.ParallelDrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

@SuppressWarnings ("Duplicates")
public class FriendlyTrenchRunGetFive extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> friendlyTrench1 = new ArrayList<>();
		friendlyTrench1.add(new Pose2d(140, 60, Rotation2d.fromDegrees(0)));
		friendlyTrench1.add(new Pose2d(170, 60, Rotation2d.fromDegrees(0)));
		friendlyTrench1.add(new Pose2d(200, 60, Rotation2d.fromDegrees(0)));

		List<Pose2d> aroundTrench = new ArrayList<>();
		aroundTrench.add(new Pose2d(200, 0, Rotation2d.fromDegrees(0)));
		aroundTrench.add(new Pose2d(300, 0, Rotation2d.fromDegrees(0)));
		aroundTrench.add(new Pose2d(300, 50, Rotation2d.fromDegrees(0)));

		aroundTrench.add(new Pose2d(300, 70, Rotation2d.fromDegrees(0)));

		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(friendlyTrench1), 0.8));
		routines.add(new DrivePathRoutine(aroundTrench));

		return new SequentialRoutine(routines);
	}
}
