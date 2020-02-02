package com.palyrobotics.frc2020.util.control;

import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class TimedSolenoid extends Solenoid {

	private final double mExtensionDurationSeconds;
	private final boolean mIsExtendedByDefault;
	private Timer mTimer = new Timer();
	private boolean mIsExtended;

	public TimedSolenoid(int channel, double extensionDurationSeconds, boolean isExtendedByDefault) {
		super(channel);
		mExtensionDurationSeconds = extensionDurationSeconds;
		mIsExtendedByDefault = isExtendedByDefault;
	}

	public void updateExtended(boolean isOn) {
		// Account for default state of piston given solenoid state
		boolean shouldBeExtended = mIsExtendedByDefault == isOn;
		if (mIsExtended != shouldBeExtended && Util.approximatelyEqual(mTimer.get(), 0)) {
			mTimer.start();
		}
		if (mTimer.get() > mExtensionDurationSeconds) {
			mIsExtended = shouldBeExtended;
			mTimer.reset();
			mTimer.stop();
		}
	}

	public boolean isExtended() {
		return mIsExtended;
	}
}
