package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.OneUpdateRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.geometry.Pose2d;

public class SetOdometryRoutine extends OneUpdateRoutine {

	private Pose2d mPose;

	public SetOdometryRoutine() {
		this(0.0, 0.0, 0.0);
	}

	public SetOdometryRoutine(double xInches, double yInches, double headingDegrees) {
		mPose = Util.newPose(xInches, yInches, headingDegrees);
	}

	@Override
	protected void updateOnce(Commands commands, @ReadOnly RobotState state) {
		commands.driveWantedOdometryPose = mPose;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
