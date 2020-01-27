package com.palyrobotics.frc2020.util.control;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Wrapper class for two solenoids controlling a single double-acting piston
 *
 * @author Luca Manolache, Jason Liu
 */
public class DualSolenoid {

	private Solenoid mExtendingSolenoid, mRetractingSolenoid;

	public enum State {
		FORWARD, REVERSE, OFF
	}

	public DualSolenoid(int extendingSolenoidID, int retractingSolenoidID) {
		mExtendingSolenoid = new Solenoid(extendingSolenoidID);
		mRetractingSolenoid = new Solenoid(retractingSolenoidID);
	}

	public void set(State state) {
		switch (state) {
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
