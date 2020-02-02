package com.palyrobotics.frc2020.util.control;

import com.palyrobotics.frc2020.util.Util;

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
	private boolean mIsExtended;

	/**
	 * @param channel                  PCM channel 0-7.
	 * @param extensionDurationSeconds Estimated time in seconds it takes the piston to retract or
	 *                                 extend.
	 * @param isExtendedByDefault      Whether or not the default solenoid state causes the piston to
	 *                                 extend.
	 */
	public TimedSolenoid(int channel, double extensionDurationSeconds, boolean isExtendedByDefault) {
		super(channel);
		mExtensionDurationSeconds = extensionDurationSeconds;
		mIsExtendedByDefault = isExtendedByDefault;
	}

	/**
	 * Updates piston(s) extension state based on how long our solenoid has been in a specific on/off
	 * state.
	 *
	 * @param isOn Solenoid on/off state
	 */
	public void updateExtended(boolean isOn) {
		// Account for default state of piston(s) given solenoid state
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

	/**
	 * @return Whether or not the piston(s) controlled by this solenoid are extended.
	 */
	public boolean isExtended() {
		return mIsExtended;
	}
}
