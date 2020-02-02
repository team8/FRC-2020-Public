package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToHoodState;
import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTargetDistanceToVelocity;
import static com.palyrobotics.frc2020.util.Util.kEpsilon;
import static com.palyrobotics.frc2020.util.Util.withinRange;

import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.Timer;

public class Shooter extends SubsystemBase {

	public enum ShooterState {
		IDLE, MANUAL_VELOCITY, VISION_VELOCITY
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
	private boolean mInVelocityRange;

	private Shooter() {
	}

	public static Shooter getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		/* Flywheel Velocity */
		double targetVelocity;
		ShooterState wantedState = commands.getShooterWantedState();
		switch (wantedState) {
			case MANUAL_VELOCITY:
				targetVelocity = commands.getShooterManualWantedFlywheelVelocity();
				break;
			case VISION_VELOCITY:
				targetVelocity = kTargetDistanceToVelocity.getInterpolated(mLimelight.getCorrectedEstimatedDistanceZ());
				break;
			default:
				targetVelocity = 0.0;
				break;
		}
		targetVelocity = Util.clamp(targetVelocity, 0.0, mConfig.maxVelocity);
		/* Hood */
		HoodState targetHoodState = kTargetDistanceToHoodState.floorEntry(targetVelocity).getValue();
		boolean isHoodExtended = robotState.shooterHoodSolenoidState.isExtended(),
				isBlockingExtended = robotState.shooterBlockingSolenoidState.isExtended();
		switch (targetHoodState) {
			case LOW:
				// TODO: are we even ble to release lock and go down at the same time?
				// mHoodOutput = isBlockingExtended;
				// When we are down, always make sure our locking piston is set to unblocking.
				// This is how other states tell if we are down instead of just resting on top
				// of the block, since the hood piston is retracted in those two cases
				// meaning its extension state can't be used to determine physical position.
				mHoodOutput = mBlockingOutput = false;
				break;
			case MIDDLE:
				if (isBlockingExtended) {
					// We are at the top hood position. See low case for how we can determine this.
					mHoodOutput = false;
					mBlockingOutput = true;
				} else {
					// We are at the low hood position.
					mHoodOutput = true;
					// Unblock until we reach the top.
					// Then block, which moves to first if condition and moves hood down to rest on
					// top of blocking piston.
					mBlockingOutput = isHoodExtended;
				}
				break;
			case HIGH:
				// Assuming we will never bee in the state where our blocking is extended and
				// our hood is pushing upwards against it.
				mHoodOutput = mBlockingOutput = true;
				break;
		}
		mFlywheelOutput.setTargetVelocity(targetVelocity, mConfig.velocityGains);
		/* Rumble */
		boolean inVelocityRange = withinRange(targetVelocity, robotState.shooterVelocity, mConfig.velocityTolerance);
		boolean inRangeStateChanged = mInVelocityRange != inVelocityRange;
		mInVelocityRange = inVelocityRange;
		switch (wantedState) {
			case MANUAL_VELOCITY:
			case VISION_VELOCITY:
				boolean justEnteredRange = inRangeStateChanged && inVelocityRange,
						justExitedRange = inRangeStateChanged && !inVelocityRange;
				if (targetVelocity > kEpsilon && justEnteredRange) {
					// Just entered into acceptable velocity change
					mRumbleOutput = true;
					mRumbleTimer.reset();
					mRumbleTimer.start();
				} else if (mRumbleTimer.get() > mConfig.rumbleDurationSeconds || justExitedRange) {
					mRumbleTimer.stop();
					mRumbleOutput = false;
				}
				break;
			default:
				mRumbleOutput = false;
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
}
