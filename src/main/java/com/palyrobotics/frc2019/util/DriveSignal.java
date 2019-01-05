package com.palyrobotics.frc2018.util;

public class DriveSignal {
	public TalonSRXOutput leftMotor;
	public TalonSRXOutput rightMotor;

	public DriveSignal(TalonSRXOutput left, TalonSRXOutput right) {
		this.leftMotor = left;
		this.rightMotor = right;
	}

	public static DriveSignal getNeutralSignal() {
		TalonSRXOutput leftNeutral = new TalonSRXOutput();
		TalonSRXOutput rightNeutral = new TalonSRXOutput();
		leftNeutral.setPercentOutput(0);
		rightNeutral.setPercentOutput(0);

		return new DriveSignal(leftNeutral, rightNeutral);
	}

	@Override
	public boolean equals(Object obj) {
		return ((DriveSignal) obj).leftMotor.equals(this.leftMotor) && ((DriveSignal) obj).rightMotor.equals(this.rightMotor);
	}

	@Override
	public String toString() {
		return "left:" + leftMotor.toString() + " right:" + rightMotor.toString();
	}
}