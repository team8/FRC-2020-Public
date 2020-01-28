package com.palyrobotics.frc2020.util;

import edu.wpi.first.wpilibj.Timer;

public class SolenoidState {

	private static final double kChangeDurationSeconds = 0.1;
	private Timer mTimer = new Timer();
	private boolean mIsExtended;

	public boolean isExtended() {
		return mIsExtended;
	}

	public void setExtended(boolean isExtended) {
		if (mIsExtended != isExtended && mTimer.get() == 0) {
			mTimer.start();
		}
		if (mTimer.get() > kChangeDurationSeconds) {
			mIsExtended = isExtended;
			mTimer.reset();
			mTimer.stop();
		}
	}
}
