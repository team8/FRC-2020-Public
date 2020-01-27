package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.OneUpdateRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

public class InitializeOdometryRoutine extends OneUpdateRoutine {

	private Pose2d mInitialPose;

	public InitializeOdometryRoutine() {
		this(0.0, 0.0, 0.0);
	}

	public InitializeOdometryRoutine(double xInches, double yInches, double headingDegrees) {
		mInitialPose = new Pose2d(Units.inchesToMeters(xInches), Units.inchesToMeters(yInches),
				Rotation2d.fromDegrees(headingDegrees));
	}

	@Override
	protected void updateOnce(Commands commands) {
		// TODO implement in accordance with sctructure
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
