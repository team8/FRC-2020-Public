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
import edu.wpi.first.wpilibj.util.Units;

@SuppressWarnings ("Duplicates")
public class ShootThreeGetFiveFromRendezvous extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		// shoot 3 balls

		List<Pose2d> leftRendezvous1 = new ArrayList<>();
		leftRendezvous1
				.add(new Pose2d(Units.inchesToMeters(140), Units.inchesToMeters(-20), Rotation2d.fromDegrees(0)));
		leftRendezvous1
				.add(new Pose2d(Units.inchesToMeters(130), Units.inchesToMeters(-25), Rotation2d.fromDegrees(0)));

		List<Pose2d> rightRendezvous1 = new ArrayList<>();
		rightRendezvous1
				.add(new Pose2d(Units.inchesToMeters(115), Units.inchesToMeters(-50), Rotation2d.fromDegrees(0)));
		rightRendezvous1
				.add(new Pose2d(Units.inchesToMeters(120), Units.inchesToMeters(-70), Rotation2d.fromDegrees(0)));
		rightRendezvous1
				.add(new Pose2d(Units.inchesToMeters(125), Units.inchesToMeters(-90), Rotation2d.fromDegrees(0)));

		// TODO: check if rendezvous are where the balls are picked up and comments to
		// be added.

		routines.add(new ShootAllBallsRoutine());

		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(leftRendezvous1), 0.8));
		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rightRendezvous1), 0.8));

		routines.add(new ShootAllBallsRoutine());
		return new SequentialRoutine(routines);
	}
}
