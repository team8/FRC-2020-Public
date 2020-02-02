package com.palyrobotics.frc2020.util.control;

import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class TimedSolenoid extends Solenoid {

	public static class TimedSolenoidState {

		public static final double kChangeDurationSeconds = 0.1;
		private Timer mTimer = new Timer();
		private boolean mIsExtended;

		public boolean isExtended() {
			return mIsExtended;
		}

		public void updateExtended(boolean isExtended) {
			if (mIsExtended != isExtended && Util.approximatelyEqual(mTimer.get(), 0)) {
				mTimer.start();
			}
			if (mTimer.get() > kChangeDurationSeconds) {
				mIsExtended = isExtended;
				mTimer.reset();
				mTimer.stop();
			}
		}
	}

	private final double mExtensionDurationSeconds;
	private final boolean mIsExtendedByDefault;

	private final TimedSolenoidState mState = new TimedSolenoidState();

	public TimedSolenoid(int channel, double extensionDurationSeconds, boolean isExtendedByDefault) {
		super(channel);
		mExtensionDurationSeconds = extensionDurationSeconds;
		mIsExtendedByDefault = isExtendedByDefault;
	}

	public void updateExtended(boolean isExtended) {
		mState.updateExtended(isExtended);
	}

	public boolean isExtended() {
		return mState.isExtended();
	}
}
