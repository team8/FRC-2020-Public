package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;

public class DriveHeadingRoutine extends TimeoutRoutineBase {

	protected double mTargetHeadingDegrees;
	private DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

	public DriveHeadingRoutine() {
	}

	public DriveHeadingRoutine(double headingDegrees) {
		mTargetHeadingDegrees = headingDegrees;
	}

	@Override
	public void start(@ReadOnly RobotState state) {
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveHeadingDegrees, mTargetHeadingDegrees);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveTurn(mTargetHeadingDegrees);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return Math.abs(state.driveHeadingDegrees - mTargetHeadingDegrees) < mDriveConfig.allowableHeadingErrorDegrees;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
