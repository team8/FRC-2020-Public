package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToHoodState;
import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToVelocity;

import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Shooter extends SubsystemBase {

	public enum ShooterState {
		IDLE, MANUAL_VELOCITY, VISION_VELOCITY
	}

	public enum HoodState {
		LOW, MIDDLE, HIGH
	}

	private static Shooter sInstance = new Shooter();
	private ShooterConfig mConfig = Configs.get(ShooterConfig.class);
	private ControllerOutput mFlywheelOutput = new ControllerOutput();
	private boolean mHoodOutput, mBlockingOutput;

	private Shooter() {
	}

	public static Shooter getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		double targetVelocity;
		switch (commands.getShooterWantedState()) {
			case MANUAL_VELOCITY:
				targetVelocity = commands.getShooterManualWantedFlywheelVelocity();
				break;
			case VISION_VELOCITY:
				targetVelocity = kTargetDistanceToVelocity.getInterpolated(robotState.visionDistanceToTarget);
				break;
			default:
				targetVelocity = 0.0;
				break;
		}
		targetVelocity = Util.clamp(targetVelocity, 0.0, mConfig.maxVelocity);
		HoodState targetHoodState = kTargetDistanceToHoodState.floorEntry(targetVelocity).getValue();
		boolean isHoodExtended = robotState.shooterHoodSolenoidState.isExtended(),
				isBlockingExtended = robotState.shooterBlockingSolenoidState.isExtended();
		switch (targetHoodState) {
			case LOW:
				// TODO: are we even ble to release lock and go down at the same time?
				// mHoodOutput = isBlockingExtended;
				// When we are down, always make sure our locking piston is set to unblocking.
				// This is how we tell if we are down instead of just resting on top of the
				// lock. Since our hood piston can be in a retracted state, but we don't know if
				// we are all the way at the bottom or just resting on the hood.
				mHoodOutput = mBlockingOutput = false;
				break;
			case MIDDLE:
				if (isBlockingExtended) {
					// We are at the top hood position.
					mHoodOutput = false;
					mBlockingOutput = true;
				} else {
					// We are at the low hood position.
					mHoodOutput = true;
					// Unblock until we reach the top.
					// Then block, which moves to first if condition and moves hood down to rest on
					// top of lock.
					mBlockingOutput = isHoodExtended;
				}
				break;
			case HIGH:
				// Assuming we will never bee in the state where our blocking is extended and
				// our hood is retracted.
				mHoodOutput = mBlockingOutput = true;
				break;
		}
		mFlywheelOutput.setTargetVelocity(targetVelocity, mConfig.velocityGains);
	}

	public ControllerOutput getFlywheelOutput() {
		return mFlywheelOutput;
	}

	public boolean getHoodOutput() {
		return mHoodOutput;
	}

	public boolean getBlockingOutput() {
		return mBlockingOutput;
	}
}
