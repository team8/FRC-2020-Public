package com.palyrobotics.frc2019.util;

public class SparkSignal {
    public SparkMaxOutput leftMotor;
    public SparkMaxOutput rightMotor;

    public SparkSignal(SparkMaxOutput left, SparkMaxOutput right) {
        this.leftMotor = left;
        this.rightMotor = right;
    }

    public static SparkSignal getNeutralSignal() {
        SparkMaxOutput leftNeutral = new SparkMaxOutput();
        SparkMaxOutput rightNeutral = new SparkMaxOutput();
        leftNeutral.setPercentOutput(0);
        rightNeutral.setPercentOutput(0);

        return new SparkSignal(leftNeutral, rightNeutral);
    }

    @Override
    public boolean equals(Object obj) {
        return ((SparkSignal) obj).leftMotor.equals(this.leftMotor) && ((SparkSignal) obj).rightMotor.equals(this.rightMotor);
    }

    @Override
    public String toString() {
        return "left:" + leftMotor.toString() + " right:" + rightMotor.toString();
    }
}
