package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;

public class DriveYawRoutine extends TimeoutRoutineBase {

	protected double mTargetYawDegrees;
	private DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

	public DriveYawRoutine() {
	}

	/**
	 * Yaw is relative to absolute odometry rotation, not relative to current
	 * rotation.
	 */
	public DriveYawRoutine(double yawDegrees) {
		mTargetYawDegrees = yawDegrees;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveYawDegrees, mTargetYawDegrees);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveYaw(mTargetYawDegrees);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		// TODO: check velocity as well
		return Math.abs(Util.getDifferenceInAngleDegrees(state.driveYawDegrees,
				mTargetYawDegrees)) < mDriveConfig.allowableYawErrorDegrees;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
