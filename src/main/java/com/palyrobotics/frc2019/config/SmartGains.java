package com.palyrobotics.frc2019.config;

public class SmartGains extends Gains {

    public static final SmartGains emptyGains = new SmartGains();

    public double acceleration, velocity;

    private SmartGains() {
        super();
        this.acceleration = 0.0;
        this.velocity = 0.0;
    }

    public SmartGains(double p, double i, double d, double f, int iZone, double rampRate, double acceleration, double velocity) {
        super(p, i, d, f, iZone, rampRate);
        this.acceleration = acceleration;
        this.velocity = velocity;
    }

    @Override // Auto-generated
    public String toString() {
        return String.format("SmartGains{acceleration=%s, velocity=%s, p=%s, i=%s, d=%s, f=%s, rampRate=%s, iZone=%d}", acceleration, velocity, p, i, d, f, rampRate, iZone);
    }
}
