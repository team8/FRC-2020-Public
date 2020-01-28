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
import com.palyrobotics.frc2020.util.control.DualSolenoid;

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
	private DualSolenoid.Output mHoodOutput = DualSolenoid.Output.REVERSE;
	private boolean mBlockingOutput;

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
		boolean isHoodExtended = robotState.shooterHoodSolenoidState.isExtended();
		switch (targetHoodState) {
			case LOW:
				mHoodOutput = DualSolenoid.Output.REVERSE;
				mBlockingOutput = false;
				break;
			case MIDDLE:
				mBlockingOutput = isHoodExtended;
				mHoodOutput = isHoodExtended ? DualSolenoid.Output.REVERSE : DualSolenoid.Output.FORWARD;
				break;
			case HIGH:
				mHoodOutput = DualSolenoid.Output.FORWARD;
				mBlockingOutput = isHoodExtended;
				break;
		}
		mFlywheelOutput.setTargetVelocity(targetVelocity, mConfig.velocityGains);
	}

	public ControllerOutput getFlywheelOutput() {
		return mFlywheelOutput;
	}

	public DualSolenoid.Output getHoodOutput() {
		return mHoodOutput;
	}

	public boolean getBlockingOutput() {
		return mBlockingOutput;
	}
}
