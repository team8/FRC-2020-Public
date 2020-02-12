package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Shooter.HoodState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class ShooterCustomVelocityRoutine extends TimeoutRoutineBase {

	private final double mTargetVelocity;
	private final HoodState mHoodState;

	public ShooterCustomVelocityRoutine(double durationSeconds, double velocity, HoodState hoodState) {
		super(durationSeconds);
		mTargetVelocity = velocity;
		mHoodState = hoodState;
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		commands.setShooterCustomFlywheelVelocity(mTargetVelocity, mHoodState);
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.setShooterIdle();
		commands.visionWanted = false;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mShooter);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}
}
