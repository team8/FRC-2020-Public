package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToHoodState;
import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToVelocity;
import static com.palyrobotics.frc2020.util.Util.kEpsilon;
import static com.palyrobotics.frc2020.util.Util.withinRange;

import java.util.Map;

import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.service.TelemetryService;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.MedianFilter;
import edu.wpi.first.wpilibj.Timer;

public class Shooter extends SubsystemBase {

	public enum ShooterState {
		IDLE, CUSTOM_VELOCITY, VISION_VELOCITY
	}

	public enum HoodState {
		LOW, MIDDLE, HIGH
	}

	private static Shooter sInstance = new Shooter();
	private Limelight mLimelight = Limelight.getInstance();
	private ShooterConfig mConfig = Configs.get(ShooterConfig.class);
	private ControllerOutput mFlywheelOutput = new ControllerOutput();
	private boolean mHoodOutput, mBlockingOutput, mRumbleOutput;
	private Timer mRumbleTimer = new Timer();
	private boolean mIsReadyToShoot;
	// TODO: Change the size of the median filter to better or worse filter out values
	private MedianFilter distanceFilter = new MedianFilter(5);

	private Shooter() {
	}

	public static Shooter getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		/* Flywheel Velocity */
		ShooterState wantedState = commands.getShooterWantedState();
		double targetFlywheelVelocity;
		Double targetDistanceInches;
		switch (wantedState) {
			case CUSTOM_VELOCITY:
				targetDistanceInches = null;
				targetFlywheelVelocity = commands.getShooterWantedCustomFlywheelVelocity();
				break;
			case VISION_VELOCITY:
				if (mLimelight.isTargetFound()) {
					targetDistanceInches = distanceFilter.calculate(mLimelight.getEstimatedDistanceZ());
					targetFlywheelVelocity = kTargetDistanceToVelocity.getInterpolated(targetDistanceInches);
				} else {
					targetDistanceInches = null;
					targetFlywheelVelocity = commands.getShooterWantedCustomFlywheelVelocity();
				}
				break;
			default:
				targetDistanceInches = null;
				targetFlywheelVelocity = 0.0;
				break;
		}
		targetFlywheelVelocity = Util.clamp(targetFlywheelVelocity, 0.0, mConfig.maxVelocity);
		TelemetryService.putArbitrary("shooter.targetDistance", targetDistanceInches);
		TelemetryService.putArbitrary("shooter.targetVelocity", targetFlywheelVelocity);
		boolean shouldUpdateHood = !state.shooterHoodIsInTransition && targetFlywheelVelocity > kEpsilon;
		if (shouldUpdateHood) updateHood(commands, state, targetDistanceInches);
		mFlywheelOutput.setTargetVelocity(targetFlywheelVelocity, mConfig.velocityGains);
		updateRumble(commands, state, targetFlywheelVelocity);
	}

	private void updateRumble(@ReadOnly Commands commands, @ReadOnly RobotState state, double targetFlywheelVelocity) {
		boolean inShootingVelocityRange = targetFlywheelVelocity > kEpsilon &&
				withinRange(targetFlywheelVelocity, state.shooterFlywheelVelocity, mConfig.velocityTolerance);
		boolean justChangedReadyToShoot = mIsReadyToShoot != inShootingVelocityRange;
		mIsReadyToShoot = inShootingVelocityRange && !state.shooterHoodIsInTransition;
		switch (commands.getShooterWantedState()) {
			case CUSTOM_VELOCITY:
			case VISION_VELOCITY:
				boolean justEnteredReadyToShoot = justChangedReadyToShoot && inShootingVelocityRange,
						justExitedReadyToShoot = justChangedReadyToShoot && !inShootingVelocityRange;
				if (justEnteredReadyToShoot) {
					mRumbleOutput = true;
					mRumbleTimer.reset();
					mRumbleTimer.start();
				} else if (mRumbleTimer.get() > mConfig.rumbleDurationSeconds || justExitedReadyToShoot) {
					mRumbleTimer.stop();
					mRumbleOutput = false;
				}
				break;
			default:
				mRumbleOutput = false;
				break;
		}
	}

	private void updateHood(Commands commands, @ReadOnly RobotState state, Double targetDistanceInches) {
		boolean isHoodExtended = state.shooterIsHoodExtended,
				isBlockingExtended = state.shooterIsBlockingExtended;
		HoodState targetHoodState;
		if (targetDistanceInches == null) {
			targetHoodState = commands.getShooterWantedHoodState();
		} else {
			Map.Entry<Double, HoodState> floorEntry = kTargetDistanceToHoodState.floorEntry(targetDistanceInches),
					ceilingEntry = kTargetDistanceToHoodState.ceilingEntry(targetDistanceInches),
					closestEntry = targetDistanceInches - floorEntry.getKey() > ceilingEntry.getKey() - targetDistanceInches ? ceilingEntry : floorEntry;
			double deltaFromThreshold = Math.abs(targetDistanceInches - closestEntry.getKey());
			targetHoodState = deltaFromThreshold > mConfig.hoodSwitchDistanceThreshold ? floorEntry.getValue() : closestEntry.getValue();
		}
		TelemetryService.putArbitrary("shooter.hoodState", targetHoodState);
		switch (targetHoodState) {
			case LOW: {
				/*
				When we are down, always make sure our locking piston is set to unblocking.
				This is how other states tell if we are down instead of just resting on top
				of the block, since the hood piston is retracted in case those two cases,
				meaning its extension state can't be used to determine physical position.
				*/
				mHoodOutput = mBlockingOutput = false;
				break;
			}
			case MIDDLE:
				if (isBlockingExtended) {
					/* Hood is already at the top or middle state */
					mHoodOutput = false;
					mBlockingOutput = true;
				} else {
					/* We are at the low hood position. */
					mHoodOutput = true;
					/*
					Unblock until the hood reaches the top, then block.
					This moves to the first if condition and moves the
					hood down to rest on top of the blocking piston.
					*/
					mBlockingOutput = isHoodExtended;
				}
				break;
			case HIGH: {
				/*
				This assumes that we will never be in the state where
				our blocking piston is extended and our hood is pushing
				upwards against it.
				*/
				mHoodOutput = true;
				if (isBlockingExtended) {
					// If we are in middle state continue locking
					mBlockingOutput = true;
				} else {
					// We are in bottom state, wait until hood is fully extended to lock
					mBlockingOutput = isHoodExtended;
				}
				break;
			}
		}
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

	public boolean getRumbleOutput() {
		return mRumbleOutput;
	}

	public boolean isReadyToShoot() {
		return mIsReadyToShoot;
	}
}
