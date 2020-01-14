package com.palyrobotics.frc2020.util.control;

import java.util.Objects;

public class DriveSignal {

    public ControllerOutput leftOutput, rightOutput;

    public DriveSignal() {
        leftOutput = new ControllerOutput();
        rightOutput = new ControllerOutput();
    }

    public DriveSignal(ControllerOutput leftOutput, ControllerOutput rightOutput) {
        this.leftOutput = leftOutput;
        this.rightOutput = rightOutput;
    }

    @Override // Auto-generated
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        DriveSignal otherSpark = (DriveSignal) other;
        return leftOutput.equals(otherSpark.leftOutput) && rightOutput.equals(otherSpark.rightOutput);
    }

    @Override // Auto-generated
    public int hashCode() {
        return Objects.hash(leftOutput, rightOutput);
    }

    @Override // Auto-generated
    public String toString() {
        return String.format("SparkDriveSignal{leftOutput=%s, rightOutput=%s}", leftOutput, rightOutput);
    }
}
