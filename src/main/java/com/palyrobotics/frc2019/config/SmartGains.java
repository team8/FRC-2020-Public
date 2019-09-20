package com.palyrobotics.frc2019.config;

public class SmartGains extends Gains {

    public static final SmartGains emptyGains = new SmartGains();

    public double acceleration, velocity;

    public SmartGains() {
        this(0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0);
    }

    public SmartGains(double p, double i, double d, double f, int iZone, double rampRate, double acceleration, double velocity) {
        super(p, i, d, f, iZone, rampRate);
        this.acceleration = acceleration;
        this.velocity = velocity;
    }
}
