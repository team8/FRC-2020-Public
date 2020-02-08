package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Shooter.HoodState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class ShooterCustomVelocityRoutine extends TimedRoutine {

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
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mShooter);
	}
}
