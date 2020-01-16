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
public class ShootThreeEnemyTrenchTwoRightRendezvousThreeShootFiveGoToOtherSide extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> enemyTrench = new ArrayList<>();
		enemyTrench.add(new Pose2d(Units.inchesToMeters(120), Units.inchesToMeters(-210), Rotation2d.fromDegrees(0)));
		enemyTrench.add(new Pose2d(Units.inchesToMeters(120), Units.inchesToMeters(-200), Rotation2d.fromDegrees(0)));

		List<Pose2d> rendezvous = new ArrayList<>();
		rendezvous.add(new Pose2d(Units.inchesToMeters(120), Units.inchesToMeters(-90), Rotation2d.fromDegrees(0)));
		rendezvous.add(new Pose2d(Units.inchesToMeters(110), Units.inchesToMeters(-70), Rotation2d.fromDegrees(0)));
		rendezvous.add(new Pose2d(Units.inchesToMeters(100), Units.inchesToMeters(-60), Rotation2d.fromDegrees(0)));

		List<Pose2d> goToOtherSide = new ArrayList<>();
		goToOtherSide.add(new Pose2d(Units.inchesToMeters(350), Units.inchesToMeters(0), Rotation2d.fromDegrees(0)));

		routines.add(new ShootAllBallsRoutine());
		routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(enemyTrench), 0.8));
		routines.add(new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rendezvous), 0.8));
		routines.add(new ShootAllBallsRoutine());
		routines.add(
				new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(goToOtherSide), 0.8));

		return new SequentialRoutine(routines);
	}
}
