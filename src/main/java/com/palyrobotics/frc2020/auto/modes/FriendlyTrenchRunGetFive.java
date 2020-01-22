package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveParallelPathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

@SuppressWarnings ("Duplicates")
public class FriendlyTrenchRunGetFive extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {
		List<RoutineBase> routines = new ArrayList<>();

		List<Pose2d> friendlyTrench1 = new ArrayList<>();
		friendlyTrench1.add(new Pose2d(Units.inchesToMeters(140), Units.inchesToMeters(60), Rotation2d.fromDegrees(0)));
		friendlyTrench1.add(new Pose2d(Units.inchesToMeters(170), Units.inchesToMeters(60), Rotation2d.fromDegrees(0)));
		friendlyTrench1.add(new Pose2d(Units.inchesToMeters(200), Units.inchesToMeters(60), Rotation2d.fromDegrees(0)));

		List<Pose2d> aroundTrench = new ArrayList<>();
		aroundTrench.add(new Pose2d(Units.inchesToMeters(200), Units.inchesToMeters(0), Rotation2d.fromDegrees(0)));
		aroundTrench.add(new Pose2d(Units.inchesToMeters(300), 0, Rotation2d.fromDegrees(0)));
		aroundTrench.add(new Pose2d(Units.inchesToMeters(300), Units.inchesToMeters(50), Rotation2d.fromDegrees(0)));

		aroundTrench.add(new Pose2d(Units.inchesToMeters(300), Units.inchesToMeters(70), Rotation2d.fromDegrees(0)));

		routines.add(
				new DriveParallelPathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(friendlyTrench1), 0.8));
		routines.add(new DrivePathRoutine(aroundTrench));

		return new SequentialRoutine(routines);
	}
}
