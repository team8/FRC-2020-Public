package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

public class DriveYawAlignRoutine extends DriveYawRoutine {

	private static final double kTimeoutMultiplier = 1.1;
	protected double mTargetYawDegrees;
	private DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private Limelight mLimelight = Limelight.getInstance();

	/**
	 * Yaw is relative to absolute odometry rotation, not relative to current rotation.
	 */
	public DriveYawAlignRoutine(double yawDegrees) {
		mTargetYawDegrees = yawDegrees;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveYawDegrees, mTargetYawDegrees) *
				kTimeoutMultiplier;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		if (mLimelight.isTargetFound() && Math.abs(Util.getDifferenceInAngleDegrees(state.driveYawDegrees,
				mTargetYawDegrees)) <= mVisionConfig.alignSwitchYawAngleMin) {
			commands.setDriveVisionAlign();
		} else {
			commands.setDriveYaw(mTargetYawDegrees);
		}
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
