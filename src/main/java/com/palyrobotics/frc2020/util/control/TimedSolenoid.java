package com.palyrobotics.frc2020.util.control;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 * Solenoid connected to piston(s) that uses a {@link Timer} to estimate when the piston(s)
 * physically changes extension state. It does not account for environmental errors that cause the
 * piston(s) to take more time extending or retracting than initially empirically measured.
 */
public class TimedSolenoid extends Solenoid {

	private final double mExtensionDurationSeconds;
	private final boolean mIsExtendedByDefault;
	private Timer mTimer = new Timer();
	private boolean mIsExtended, mIsInTransition;

	/**
	 * @param channel                  PCM channel 0-7.
	 * @param extensionDurationSeconds Estimated time in seconds it takes the piston to retract or
	 *                                 extend.
	 * @param isExtendedByDefault      Whether or not the default solenoid state (off) causes the piston
	 *                                 to extend. This is the extension state when the robot is
	 *                                 disabled.
	 */
	public TimedSolenoid(int channel, double extensionDurationSeconds, boolean isExtendedByDefault) {
		super(channel);
		mExtensionDurationSeconds = extensionDurationSeconds;
		mIsExtendedByDefault = isExtendedByDefault;
	}

	public void setExtended(boolean isExtended) {
		// Account for extension state of piston(s) given default solenoid state (off)
		boolean isOn = mIsExtendedByDefault != isExtended;
		set(isOn);
		updateExtended(isExtended);
	}

	/**
	 * Updates piston(s) extension state based on time elapsed since previous extension state.
	 */
	protected void updateExtended(boolean wantedIsExtended) {
		if (mIsExtended != wantedIsExtended && !mIsInTransition) {
			mIsInTransition = true;
			mTimer.reset();
			mTimer.start();
		}
		if (mTimer.hasElapsed(mExtensionDurationSeconds)) {
			mIsExtended = wantedIsExtended;
			mIsInTransition = false;
			mTimer.stop();
		}
	}

	public boolean isExtended() {
		return mIsExtended;
	}

	public boolean isInTransition() {
		return mIsInTransition;
	}
}
