package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToHoodState;
import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToVelocity;
import static com.palyrobotics.frc2020.util.Util.*;

import java.util.Map;

import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
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
	private MedianFilter distanceFilter = new MedianFilter(15);
	private MedianFilter velocityFilter = new MedianFilter(15);

	private Shooter() {
	}

	public static Shooter getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		/* Target Distance (only for vision, null otherwise */
		Double targetDistanceInches = getTargetDistanceInches(commands);
		/* Hood State */
		HoodState hoodState = updateHood(commands, state, targetDistanceInches);
		/* Flywheel Velocity */
		double targetFlywheelVelocity = getTargetFlywheelVelocity(commands, hoodState, targetDistanceInches, state);
		if (targetFlywheelVelocity > kEpsilon) {
			mFlywheelOutput.setTargetVelocity(targetFlywheelVelocity, mConfig.velocityGains);
		} else {
			mFlywheelOutput.setIdle();
		}
		/* Ready to shoot */
		boolean inShootingVelocityRange = targetFlywheelVelocity > kEpsilon &&
				withinRange(targetFlywheelVelocity, state.shooterFlywheelVelocity, mConfig.velocityTolerance);
		boolean isReadyToShoot = inShootingVelocityRange && !state.shooterHoodIsInTransition,
				justChangedReadyToShoot = mIsReadyToShoot != isReadyToShoot;
		mIsReadyToShoot = isReadyToShoot;
		/* Rumble */
		updateRumble(commands, justChangedReadyToShoot);
		/* Telemetry */
		LiveGraph.add("shooterTargetDistance", targetDistanceInches == null ? -1 : targetDistanceInches);
		LiveGraph.add("shooterTargetVelocity", targetFlywheelVelocity);
		LiveGraph.add("shooterCurrentVelocity", state.shooterFlywheelVelocity);
//		TelemetryService.putArbitrary("shooterTargetDistance", targetDistanceInches);
//		TelemetryService.putArbitrary("shooterTargetVelocity", targetFlywheelVelocity);
//		TelemetryService.putArbitrary("shooterFlywheelVelocity", state.shooterFlywheelVelocity);
	}

	private Double getTargetDistanceInches(@ReadOnly Commands commands) {
		if (commands.getShooterWantedState() == ShooterState.VISION_VELOCITY && mLimelight.isTargetFound()) {
			return distanceFilter.calculate(mLimelight.getEstimatedDistanceInches());
		}
		return null;
	}

	private double getTargetFlywheelVelocity(@ReadOnly Commands commands, HoodState hoodState, Double targetDistanceInches, RobotState state) {
		double targetFlywheelVelocity;
		switch (commands.getShooterWantedState()) {
			case CUSTOM_VELOCITY:
				targetFlywheelVelocity = commands.getShooterWantedCustomFlywheelVelocity();
				break;
			case VISION_VELOCITY:
				if (targetDistanceInches == null) {
					targetFlywheelVelocity = commands.getShooterWantedCustomFlywheelVelocity();
				} else {
					targetFlywheelVelocity = kTargetDistanceToVelocity.get(hoodState).getInterpolated(targetDistanceInches);
					targetFlywheelVelocity = velocityFilter.calculate(targetFlywheelVelocity);
				}
				break;
			default:
				targetFlywheelVelocity = 0.0;
				break;
		}
		return clamp(targetFlywheelVelocity, 0.0, mConfig.maxVelocity);
	}

	// Null hood state represents no action
	private HoodState updateHood(@ReadOnly Commands commands, @ReadOnly RobotState state, Double targetDistanceInches) {
		HoodState targetHoodState;
		switch (commands.getShooterWantedState()) {
			case IDLE:
				targetHoodState = HoodState.HIGH;
				break;
			case CUSTOM_VELOCITY:
				targetHoodState = commands.getShooterWantedHoodState();
				break;
			case VISION_VELOCITY:
				if (targetDistanceInches == null) {
					targetHoodState = null;
				} else {
					Map.Entry<Double, HoodState> floorEntry = kTargetDistanceToHoodState.floorEntry(targetDistanceInches),
							ceilingEntry = kTargetDistanceToHoodState.ceilingEntry(targetDistanceInches),
							closestEntry = ceilingEntry == null || targetDistanceInches - floorEntry.getKey() < ceilingEntry.getKey() - targetDistanceInches ? floorEntry : ceilingEntry;
					double deltaFromThreshold = Math.abs(targetDistanceInches - closestEntry.getKey());
					targetHoodState = deltaFromThreshold > mConfig.hoodSwitchDistanceThreshold ? floorEntry.getValue() : closestEntry.getValue();
				}
				break;
			default:
				targetHoodState = null;
				break;
		}
		TelemetryService.putArbitrary("shooterTargetHoodState", targetHoodState);
		if (targetHoodState != null) {
			applyHoodState(state, targetHoodState);
		}
		return targetHoodState;
	}

	private void applyHoodState(@ReadOnly RobotState state, HoodState targetHoodState) {
		boolean isHoodExtended = state.shooterIsHoodExtended,
				isBlockingExtended = state.shooterIsBlockingExtended;
		switch (targetHoodState) {
			case LOW: {
				/*
				When we are down, always make sure our locking piston is set to unblocking.
				This is how other states tell if we are down instead of just resting on top
				of the block, since the hood piston is retracted in case those two cases,
				meaning its extension state can't be used to determine physical position.
				*/
				mBlockingOutput = false;
				mHoodOutput = isBlockingExtended;
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

	private void updateRumble(@ReadOnly Commands commands, boolean justChangedReadyToShoot) {
		switch (commands.getShooterWantedState()) {
			case CUSTOM_VELOCITY:
			case VISION_VELOCITY:
				boolean justEnteredReadyToShoot = justChangedReadyToShoot && mIsReadyToShoot,
						justExitedReadyToShoot = justChangedReadyToShoot && !mIsReadyToShoot;
				if (justEnteredReadyToShoot) {
					mRumbleOutput = true;
					mRumbleTimer.reset();
					mRumbleTimer.start();
				} else if (mRumbleTimer.hasElapsed(mConfig.rumbleDurationSeconds) || justExitedReadyToShoot) {
					mRumbleTimer.stop();
					mRumbleOutput = false;
				}
				break;
			default:
				mRumbleOutput = false;
				break;
		}
		TelemetryService.putArbitrary("shooterWantsRumble", mRumbleOutput);
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
