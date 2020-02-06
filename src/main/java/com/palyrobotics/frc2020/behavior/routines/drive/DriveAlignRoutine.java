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
import com.palyrobotics.frc2020.vision.LimelightControlMode;

public class DriveAlignRoutine extends DriveYawRoutine {

	private static final double kTimeoutMultiplier = 2.0;
	private VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private Limelight mLimelight = Limelight.getInstance();

	// TODO: aditya add a parameter for limelight pipeline id
	public DriveAlignRoutine(double yawDegrees) {
		super(yawDegrees);
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveYawDegrees, mTargetYawDegrees) *
				kTimeoutMultiplier;
		mLimelight.setPipeline(0);
		mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
		mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
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
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		// TODO: aditya check tx
		return false;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
