package com.palyrobotics.frc2020.auto.modes;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveParallelPathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.intake.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.shooter.ShootAllBallsRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

@SuppressWarnings ("Duplicates")
public class Shoot3RendezvousGetShoot3Loading extends AutoModeBase {

	@Override
	public RoutineBase getRoutine() {
		// start facing the 3 balls from the leftmost ball if facing the balls from op
		// station
		List<RoutineBase> routines = new ArrayList<>();

		List<Pose2d> rendezvous1 = new ArrayList<>();
		rendezvous1.add(new Pose2d(Units.inchesToMeters(91), Units.inchesToMeters(0), Rotation2d.fromDegrees(0)));

		List<Pose2d> rendezvous2 = new ArrayList<>();
		rendezvous2.add(new Pose2d(Units.inchesToMeters(98), Units.inchesToMeters(10), Rotation2d.fromDegrees(0)));

		List<Pose2d> shoot = new ArrayList<>();
		shoot.add(new Pose2d(Units.inchesToMeters(98), Units.inchesToMeters(10), Rotation2d.fromDegrees(180)));
		// turn here to face the hoop then shoot
		List<Pose2d> goSupply = new ArrayList<>();
		goSupply.add(new Pose2d(Units.inchesToMeters(328), Units.inchesToMeters(10), Rotation2d.fromDegrees(0)));
		routines.add(new DrivePathRoutine(rendezvous1));
		routines.add(new DrivePathRoutine(rendezvous2));
		routines.add(new DriveParallelPathRoutine(new IntakeBallRoutine(0.0), new DrivePathRoutine(rendezvous2), 0.8));
		// get 3 balls from the rendezvous
		routines.add(new DriveParallelPathRoutine(new ShootAllBallsRoutine(), new DrivePathRoutine(shoot), 0.8));
		// shoot ball

		return new SequentialRoutine(routines);
	}
}
