package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

public class DriveAlignRoutine extends TimeoutRoutineBase {

	private final Limelight mLimelight = Limelight.getInstance();
	private final VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final int mVisionPipeline;

	public DriveAlignRoutine(int visionPipeline) {
		super(3.0);
		mVisionPipeline = visionPipeline;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.visionWanted = true;
		commands.visionWantedPipeline = mVisionPipeline;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return Math.abs(mLimelight.getYawToTarget()) < mVisionConfig.acceptableYawError;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveVisionAlign(mVisionPipeline);
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
