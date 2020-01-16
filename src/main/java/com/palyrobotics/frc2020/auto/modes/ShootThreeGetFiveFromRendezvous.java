package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.ParallelDrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.ShootAllBallsRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

@SuppressWarnings ("Duplicates")
public class ShootThreeGetFiveFromRendezvous extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		// shoot 3 balls

		List<Pose2d> leftRendezvous1 = new ArrayList<>();
		leftRendezvous1.add(new Pose2d(140, -20, Rotation2d.fromDegrees(0)));

		List<Pose2d> leftRendezvous2 = new ArrayList<>();
		leftRendezvous2.add(new Pose2d(130, -25, Rotation2d.fromDegrees(0)));

		List<Pose2d> rightRendezvous1 = new ArrayList<>();
		rightRendezvous1.add(new Pose2d(115, -50, Rotation2d.fromDegrees(0)));

		List<Pose2d> rightRendezvous11 = new ArrayList<>();
		rightRendezvous11.add(new Pose2d(110, -60, Rotation2d.fromDegrees(0)));

		List<Pose2d> rightRendezvous2 = new ArrayList<>();
		rightRendezvous2.add(new Pose2d(120, -70, Rotation2d.fromDegrees(0)));

		List<Pose2d> rightRendezvous3 = new ArrayList<>();
		rightRendezvous3.add(new Pose2d(125, -90, Rotation2d.fromDegrees(0)));

		List<Pose2d> backToStart = new ArrayList<>();
		rightRendezvous3.add(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

		// TODO: check if rendezvous are where the balls are picked up and comments to
		// be added.

		routines.add(new ShootAllBallsRoutine());
		// shoot

		routines.add(new DrivePathRoutine(leftRendezvous1));
		routines.add(new DrivePathRoutine(leftRendezvous2));
		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(leftRendezvous2), 0.8));
		routines.add(new DrivePathRoutine(rightRendezvous1));
		routines.add(new DrivePathRoutine(rightRendezvous2));
		routines.add(new DrivePathRoutine(rightRendezvous3));
		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rightRendezvous3), 0.8));

		routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(backToStart), 0.8));

		return new SequentialRoutine(routines);
	}
}
