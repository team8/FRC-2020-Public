package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

public class DriveAlignInnerRoutine extends TimeoutRoutineBase {

	private final Limelight mLimelight = Limelight.getInstance();
	private final VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
	private double mTargetYawDegrees;
	private final int mVisionPipeline;

	/**
	 * Yaw is relative to absolute odometry rotation, not relative to current rotation.
	 */
	public DriveAlignInnerRoutine(int visionPipeline) {
		super(3.0);
		mVisionPipeline = visionPipeline;
		mTargetYawDegrees = 0.0;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.visionWanted = true;
		commands.visionWantedPipeline = mVisionPipeline;

	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return Math.abs(state.driveYawDegrees - mTargetYawDegrees) < mDriveConfig.allowableYawErrorDegrees;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		if (mLimelight.isTargetFound() && Math.abs(mLimelight.getYawToTarget()) < mVisionConfig.acceptableYawError && mTargetYawDegrees == 0.0) {
			mTargetYawDegrees = Math.toDegrees(Math.atan(mLimelight.getEstimatedDistanceInches() * Math.sin(Math.toRadians(state.driveYawDegrees)) / (mLimelight.getEstimatedDistanceInches() * Math.cos(Math.toRadians(state.driveYawDegrees)) + 29.25)));
		}

		if (mTargetYawDegrees != 0.0) {
			commands.setDriveYaw(mTargetYawDegrees);
			commands.visionWanted = false;
		} else {
			commands.setDriveVisionAlign(mVisionPipeline);
		}
		commands.lightingWantedState = Lighting.State.ROBOT_ALIGNING;
		System.out.println("robot yaw: " + state.driveYawDegrees);
		System.out.println("target yaw: " + mTargetYawDegrees);
		System.out.println("estimated distance" + mLimelight.getEstimatedDistanceInches());
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.visionWanted = false;
		commands.lightingWantedState = Lighting.State.TARGET_FOUND;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
