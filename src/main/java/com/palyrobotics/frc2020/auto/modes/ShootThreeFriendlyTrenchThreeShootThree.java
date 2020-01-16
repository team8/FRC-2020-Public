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
public class ShootThreeFriendlyTrenchThreeShootThree extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> friendlyTrench = new ArrayList<>();
		friendlyTrench.add(new Pose2d(200, 60, Rotation2d.fromDegrees(0)));
		// pick up ball
		friendlyTrench.add(new Pose2d(170, 60, Rotation2d.fromDegrees(0)));
		// pick up ball
		friendlyTrench.add(new Pose2d(140, 60, Rotation2d.fromDegrees(0)));
		// pick up ball

		List<Pose2d> shoot = new ArrayList<>();
		shoot.add(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));

		// shoot three balls
		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(friendlyTrench), 0.8));
		routines.add(new DrivePathRoutine(shoot)); // go back to shoot
		routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(shoot), 0.8));
		// shoot three balls

		return new SequentialRoutine(routines);
	}
}
