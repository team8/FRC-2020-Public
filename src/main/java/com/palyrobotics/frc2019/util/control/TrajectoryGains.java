package com.palyrobotics.frc2019.util.control;

public class TrajectoryGains {

    public double v, a, s, p, i, d;

    public TrajectoryGains() {

    }

    @Override
    public String toString() { // Auto-generated
        return String.format("TrajectoryGains{v=%f, a=%f, s=%f, p=%f, i=%f, d=%f}", v, a, s, p, i, d);
    }
}
