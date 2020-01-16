package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.ShootAllBallsRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

@SuppressWarnings ("Duplicates")
public class ShootThreeGoToFriendlyLoadingStation extends AutoModeBase {

	@Override
	public Routine getRoutine() {
		List<Routine> routines = new ArrayList<>();

		List<Pose2d> otherSide = new ArrayList<>();
		otherSide.add(new Pose2d(Units.inchesToMeters(550), Units.inchesToMeters(0), Rotation2d.fromDegrees(0)));

		routines.add(new ShootAllBallsRoutine());
		routines.add(new DrivePathRoutine(otherSide));

		return new SequentialRoutine(routines);
	}
}
