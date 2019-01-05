package com.palyrobotics.frc2018.util;

public class ClimberSignal {

	public double velocity;

	public boolean brake, latchLock;

	public ClimberSignal(double velocity, boolean brake, boolean latchLock) {
		this.brake = brake;
		this.latchLock = latchLock;
		this.velocity = velocity;
	}
	
	public static ClimberSignal getNeutralSignal() {
		return new ClimberSignal(0, true, false);
	}
}