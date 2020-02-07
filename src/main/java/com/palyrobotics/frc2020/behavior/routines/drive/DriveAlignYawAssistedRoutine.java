package com.palyrobotics.frc2020.behavior.routines.drive;

import static com.palyrobotics.frc2020.util.Util.getDifferenceInAngleDegrees;

import java.util.Set;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

public class DriveAlignYawAssistedRoutine extends DriveYawRoutine {

	private static final double kTimeoutMultiplier = 2.0;
	private final VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final Limelight mLimelight = Limelight.getInstance();
	private final int mVisionPipeline;

	public DriveAlignYawAssistedRoutine(double yawDegrees, int visionPipeline) {
		super(yawDegrees);
		mVisionPipeline = visionPipeline;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveYawDegrees, mTargetYawDegrees) *
				kTimeoutMultiplier;
		commands.visionWanted = true;
		commands.visionWantedPipeline = mVisionPipeline;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		double yawErrorDegrees = getDifferenceInAngleDegrees(state.driveYawDegrees, mTargetYawDegrees);
		if (mLimelight.isTargetFound() && Math.abs(yawErrorDegrees) < mVisionConfig.alignSwitchYawAngleMin) {
			commands.setDriveVisionAlign();
		} else {
			commands.setDriveYaw(mTargetYawDegrees);
		}
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.visionWanted = false;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return Math.abs(mLimelight.getYawToTarget()) < mVisionConfig.acceptableYawError;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
