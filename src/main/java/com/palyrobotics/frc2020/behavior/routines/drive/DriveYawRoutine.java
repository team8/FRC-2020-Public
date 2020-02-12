package com.palyrobotics.frc2020.behavior.routines.drive;

import static com.palyrobotics.frc2020.util.Util.getDifferenceInAngleDegreesNeg180To180;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;

public class DriveYawRoutine extends TimeoutRoutineBase {

	private static final double kTimeoutMultiplier = 1.1;
	protected double mTargetYawDegrees;
	private DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

	public DriveYawRoutine() {
	}

	/**
	 * Yaw is relative to absolute odometry rotation, not relative to current rotation.
	 */
	public DriveYawRoutine(double yawDegrees) {
		mTargetYawDegrees = yawDegrees;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveYawDegrees, mTargetYawDegrees) *
				kTimeoutMultiplier;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveYaw(mTargetYawDegrees);
		LiveGraph.add("mTargetYawDegrees", mTargetYawDegrees);
		CSVWriter.addData("mTargetYawDegrees", mTargetYawDegrees);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		// TODO: check velocity as well
		double yawErrorDegrees = getDifferenceInAngleDegreesNeg180To180(state.driveYawDegrees, mTargetYawDegrees);
		return Math.abs(yawErrorDegrees) < mDriveConfig.allowableYawErrorDegrees;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
