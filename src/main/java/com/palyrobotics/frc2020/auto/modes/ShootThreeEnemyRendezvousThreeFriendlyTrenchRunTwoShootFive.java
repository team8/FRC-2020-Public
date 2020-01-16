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
public class ShootThreeEnemyRendezvousThreeFriendlyTrenchRunTwoShootFive extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> shoot1 = new ArrayList<>();
		shoot1.add(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

		List<Pose2d> enemyrendezvous = new ArrayList<>();
		enemyrendezvous.add(new Pose2d(9, -2, Rotation2d.fromDegrees(0)));
		enemyrendezvous.add(new Pose2d(8.5, -1.5, Rotation2d.fromDegrees(0)));
		enemyrendezvous.add(new Pose2d(8, -1, Rotation2d.fromDegrees(0)));

		List<Pose2d> aroundTrench = new ArrayList<>();
		aroundTrench.add(new Pose2d(7.5, 1, Rotation2d.fromDegrees(0)));

		aroundTrench.add(new Pose2d(7.5, 1.5, Rotation2d.fromDegrees(0)));

		routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(shoot1), 0.8));

		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(enemyrendezvous), 0.8));

		routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(aroundTrench), 0.8));

		routines.add(new ShootAllBallsRoutine());

		return new SequentialRoutine(routines);
	}
}
