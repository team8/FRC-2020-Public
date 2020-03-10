package com.palyrobotics.frc2020.util.control;

import java.util.Objects;

@SuppressWarnings ("java:S1104")
public class DriveOutputs {

	public ControllerOutput leftOutput, rightOutput;

	public DriveOutputs() {
		leftOutput = new ControllerOutput();
		rightOutput = new ControllerOutput();
	}

	public DriveOutputs(ControllerOutput leftOutput, ControllerOutput rightOutput) {
		this.leftOutput = leftOutput;
		this.rightOutput = rightOutput;
	}

	@Override // Auto-generated
	public int hashCode() {
		return Objects.hash(leftOutput, rightOutput);
	}

	@Override // Auto-generated
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		DriveOutputs otherSpark = (DriveOutputs) other;
		return leftOutput.equals(otherSpark.leftOutput) && rightOutput.equals(otherSpark.rightOutput);
	}

	@Override // Auto-generated
	public String toString() {
		return String.format("SparkDriveSignal{leftOutput=%s, rightOutput=%s}", leftOutput, rightOutput);
	}
}
