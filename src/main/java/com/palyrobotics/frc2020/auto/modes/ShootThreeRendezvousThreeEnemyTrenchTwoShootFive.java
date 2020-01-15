package com.palyrobotics.frc2020.auto.modes;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.ParallelDrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.ShootAllBallsRoutine;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class ShootThreeRendezvousThreeEnemyTrenchTwoShootFive extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> enemyTrench = new ArrayList<>();
		enemyTrench.add(new Pose2d(120, -210, Rotation2d.fromDegrees(0)));
		// pick up ball
		enemyTrench.add(new Pose2d(120, -200, Rotation2d.fromDegrees(0)));
		// pick up ball

		List<Pose2d> rendezvous = new ArrayList<>();
		rendezvous.add(new Pose2d(120, -90, Rotation2d.fromDegrees(0)));
		// pick up ball
		rendezvous.add(new Pose2d(110, -70, Rotation2d.fromDegrees(0)));
		// pick up ball
		rendezvous.add(new Pose2d(100, -60, Rotation2d.fromDegrees(0)));
		// pick up ball

		List<Pose2d> goBackToStart = new ArrayList<>();
		goBackToStart.add(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

		// shoot 3 balls
		routines.add(new ShootAllBallsRoutine());
		// pick up 2 balls
		routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(enemyTrench), 0.8));
		routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rendezvous), 0.8));
		routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(goBackToStart), 0.8));

		return new SequentialRoutine(routines);
	}
}
