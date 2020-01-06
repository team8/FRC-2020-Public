package com.palyrobotics.frc2020.util.control;

import java.util.Objects;

public class Gains {

    public double p, i, d, f, iZone;

    public Gains() {
    }

    public Gains(double p, double i, double d, double f, double iZone) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        this.iZone = iZone;
    }

    @Override // Auto-generated
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Gains)) return false;
        Gains otherGains = (Gains) other;
        return Double.compare(otherGains.p, p) == 0 &&
                Double.compare(otherGains.i, i) == 0 &&
                Double.compare(otherGains.d, d) == 0 &&
                Double.compare(otherGains.f, f) == 0 &&
                Double.compare(otherGains.iZone, iZone) == 0;
    }

    @Override // Auto-generated
    public int hashCode() {
        return Objects.hash(p, i, d, f, iZone);
    }

    @Override // Auto-generated
    public String toString() {
        return String.format("Gains{p=%f, i=%f, d=%f, f=%f, iZone=%f}", p, i, d, f, iZone);
    }
}
