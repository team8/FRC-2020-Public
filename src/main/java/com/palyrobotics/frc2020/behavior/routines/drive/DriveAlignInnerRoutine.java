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

	// Distance from the front of the target to the plane that the inner circle is on
	public static final double kInnerOuterDistanceInches = 29.25;
	private final Limelight mLimelight = Limelight.getInstance();
	private final VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
	private Double mTargetYawDegrees;
	private final int mVisionPipeline;

	/**
	 * Yaw is relative to absolute odometry rotation, not relative to current rotation.
	 */
	public DriveAlignInnerRoutine(int visionPipeline) {
		super(3.0);
		mVisionPipeline = visionPipeline;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.visionWanted = true;
		commands.visionWantedPipeline = mVisionPipeline;
		commands.lightingWantedState = Lighting.State.ROBOT_ALIGNING;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return mTargetYawDegrees != null && Math.abs(state.driveYawDegrees - mTargetYawDegrees) < mDriveConfig.allowableYawErrorDegrees;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		//sets once, when aligned to the vision target
		if (mTargetYawDegrees == null && mLimelight.isTargetFound() && Math.abs(mLimelight.getYawToTarget()) < mVisionConfig.acceptableYawError) {
			double xDist = mLimelight.getEstimatedDistanceInches() * Math.sin(Math.toRadians(state.driveYawDegrees));
			double yDist = mLimelight.getEstimatedDistanceInches() * Math.cos(Math.toRadians(state.driveYawDegrees)) + kInnerOuterDistanceInches;
			mTargetYawDegrees = Math.toDegrees(Math.atan(xDist / yDist));
		}

		//defaults to vision until vision is on target
		if (mTargetYawDegrees == null) {
			commands.setDriveVisionAlign(mVisionPipeline);
		} else {
			commands.setDriveYaw(mTargetYawDegrees);
			commands.visionWanted = false;
		}
		commands.lightingWantedState = Lighting.State.ROBOT_ALIGNING;
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
