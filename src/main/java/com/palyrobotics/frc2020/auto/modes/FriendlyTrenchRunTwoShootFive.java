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
public class FriendlyTrenchRunTwoShootFive extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> friendlyTrench = new ArrayList<>();
		friendlyTrench.add(new Pose2d(Units.inchesToMeters(170), Units.inchesToMeters(60), Rotation2d.fromDegrees(0)));
		friendlyTrench.add(new Pose2d(Units.inchesToMeters(140), Units.inchesToMeters(60), Rotation2d.fromDegrees(0)));

		List<Pose2d> shoot = new ArrayList<>();
		shoot.add(new Pose2d(Units.inchesToMeters(0), Units.inchesToMeters(0), Rotation2d.fromDegrees(0)));
		// will have to adjust this to rotate accordingly.

		routines.add(
				new ParallelDrivePathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(friendlyTrench), 0.8));
		// pick up balls from our trench
		routines.add(new ParallelDrivePathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(shoot), 0.8));
		// shoot ball

		return new SequentialRoutine(routines);
	}
}
