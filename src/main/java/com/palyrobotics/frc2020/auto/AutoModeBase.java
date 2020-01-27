package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.RoutineBase;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

public abstract class AutoModeBase {

	public abstract RoutineBase getRoutine();

	protected static Pose2d newPose(double xInches, double yInches, double headingDegrees) {
		return new Pose2d(Units.inchesToMeters(xInches), Units.inchesToMeters(yInches),
				Rotation2d.fromDegrees(headingDegrees));
	}
}
