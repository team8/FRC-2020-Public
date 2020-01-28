package com.palyrobotics.frc2020.util.control;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Wrapper class for two solenoids controlling a single double-acting piston.
 *
 * @author Luca Manolache, Jason Liu
 */
public class DualSolenoid {

	public enum Output {
		FORWARD, REVERSE, OFF
	}

	private Solenoid mExtendingSolenoid, mRetractingSolenoid;
	private Output mOutput;

	public DualSolenoid(int extendingSolenoidID, int retractingSolenoidID) {
		mExtendingSolenoid = new Solenoid(extendingSolenoidID);
		mRetractingSolenoid = new Solenoid(retractingSolenoidID);
	}

	public Output get() {
		return mOutput;
	}

	public void set(Output output) {
		mOutput = output;
		switch (output) {
			case FORWARD:
				mExtendingSolenoid.set(true);
				mRetractingSolenoid.set(false);
				break;
			case REVERSE:
				mExtendingSolenoid.set(false);
				mRetractingSolenoid.set(true);
				break;
			case OFF:
				mExtendingSolenoid.set(false);
				mRetractingSolenoid.set(false);
		}
	}
}
