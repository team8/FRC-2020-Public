package com.palyrobotics.frc2019.util;

import java.util.Objects;

public class SparkDriveSignal {

    public SparkMaxOutput leftOutput, rightOutput;

    public SparkDriveSignal() {
        leftOutput = new SparkMaxOutput();
        rightOutput = new SparkMaxOutput();
    }

    public SparkDriveSignal(SparkMaxOutput leftOutput, SparkMaxOutput rightOutput) {
        this.leftOutput = leftOutput;
        this.rightOutput = rightOutput;
    }

    @Override // Auto-generated
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SparkDriveSignal otherSpark = (SparkDriveSignal) other;
        return leftOutput.equals(otherSpark.leftOutput) && rightOutput.equals(otherSpark.rightOutput);
    }

    @Override // Auto-generated
    public int hashCode() {
        return Objects.hash(leftOutput, rightOutput);
    }

    @Override
    public String toString() {
        return String.format("Left:%s%nRight:%s", leftOutput.toString(), rightOutput.toString());
    }
}
