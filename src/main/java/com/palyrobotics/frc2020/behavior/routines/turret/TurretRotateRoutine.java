package com.palyrobotics.frc2020.behavior.routines.turret;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.subsystem.TurretConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.subsystems.Turret;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

public class TurretRotateRoutine extends TimeoutRoutineBase {

	private TurretConfig mConfig = Configs.get(TurretConfig.class);
	private final int mVisionPipeline;
	private final boolean leftDirection;

	public TurretRotateRoutine(boolean leftDirection, int visionPipeline) {
		super(5.0);
		mVisionPipeline = visionPipeline;
		this.leftDirection = leftDirection;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		commands.visionWanted = true;
		commands.visionWantedPipeline = mVisionPipeline;
		mTimer.start();
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.turretWantedState = (leftDirection) ? Turret.TurretState.ROTATING_LEFT :
				Turret.TurretState.ROTATING_RIGHT;
	}

	@Override
	protected void stop(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		commands.visionWanted = false;
	}

	/**
	 * checks whether it is turned to the target or if it hit the edge.
	 */
	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return Math
				.abs(Limelight.getInstance().getYawToTarget() - state.turretYawDegrees) < mConfig.acceptableYawError ||
				Math.abs(state.turretYawDegrees) - mConfig.maximumAngle < mConfig.acceptableYawError;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mTurret);
	}
}
