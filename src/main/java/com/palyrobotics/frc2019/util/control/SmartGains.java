package com.palyrobotics.frc2019.util.control;

import java.util.Objects;

public class SmartGains extends Gains {

    public double acceleration, velocity, allowableError, minimumOutputVelocity;

    public SmartGains() {

    }

    @Override // Auto-generated
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SmartGains)) return false;
        if (!super.equals(other)) return false;
        SmartGains otherGains = (SmartGains) other;
        return Double.compare(otherGains.acceleration, acceleration) == 0 &&
                Double.compare(otherGains.velocity, velocity) == 0 &&
                Double.compare(otherGains.allowableError, allowableError) == 0 &&
                Double.compare(otherGains.minimumOutputVelocity, minimumOutputVelocity) == 0;
    }

    @Override // Auto-generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), acceleration, velocity, allowableError, minimumOutputVelocity);
    }

    @Override
    public String toString() { // Auto-generated
        return String.format("SmartGains{acceleration=%f, velocity=%f, allowableError=%f, minimumOutputVelocity=%f} %s", acceleration, velocity, allowableError, minimumOutputVelocity, super.toString());
    }
}
