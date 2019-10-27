package com.palyrobotics.frc2019.util.control;

import java.util.Objects;

public class Gains {

    // region Legacy Gains

    // Drive Distance PID control loop
    public static final double kVidarDriveStraightTurnP = -0.06;
    private static final double kVidarDriveDistanceP = 0.5;
    private static final double kVidarDriveDistanceI = 0.0025;
    private static final double kVidarDriveDistanceD = 12.0;
    private static final int kVidarDriveDistanceIZone = 125;
    public static final Gains vidarDriveDistance = new Gains(
            kVidarDriveDistanceP, kVidarDriveDistanceI, kVidarDriveDistanceD, 0,
            kVidarDriveDistanceIZone
    );

    // Drive Motion Magic off-board control loop
    // Short distance max speed 45 in/s Max acceleration 95 in/s^2
    public static final double kVidarShortDriveMotionMagicCruiseVelocity = 60;
    public static final double kVidarShortDriveMotionMagicMaxAcceleration = 120;
    private static final double kVidarShortDriveMotionMagicP = .5;
    private static final double kVidarShortDriveMotionMagicI = 0; //0.00040 / 2;
    private static final double kVidarShortDriveMotionMagicD = 0; //275 / 2;
    private static final double kVidarShortDriveMotionMagicF = .1821; //2.075 / 2;
    private static final int kVidarShortDriveMotionMagicIZone = 0; //150 / 2;
    public static final Gains vidarShortDriveMotionMagicGains = new Gains(
            kVidarShortDriveMotionMagicP, kVidarShortDriveMotionMagicI,
            kVidarShortDriveMotionMagicD, kVidarShortDriveMotionMagicF, kVidarShortDriveMotionMagicIZone
    );

    // Drive Motion Magic turn angle gains
    public static final double kVidarTurnMotionMagicCruiseVelocity = 72;
    public static final double kVidarTurnMotionMagicMaxAcceleration = 36;
    private static final double kVidarTurnMotionMagickP = 6.0;
    private static final double kVidarTurnMotionMagickI = 0.01;
    private static final double kVidarTurnMotionMagickD = 210;
    private static final double kVidarTurnMotionMagickF = 2.0;
    private static final int kVidarTurnMotionMagickIzone = 50;
    public static final Gains vidarTurnMotionMagicGains = new Gains(
            kVidarTurnMotionMagickP, kVidarTurnMotionMagickI, kVidarTurnMotionMagickD,
            kVidarTurnMotionMagickF, kVidarTurnMotionMagickIzone
    );

    private static final double
            kVidarElevatorPositionP = 0.5, // 0.7;
            kVidarElevatorPositionI = 0.0,
            kVidarElevatorPositionD = 3.4, // 2.0;
            kVidarElevatorPositionF = 0.0,
            kVidarElevatorPositionIZone = 0.0;
    public static final Gains elevatorPosition = new Gains(
            kVidarElevatorPositionP, kVidarElevatorPositionI, kVidarElevatorPositionD, kVidarElevatorPositionF,
            kVidarElevatorPositionIZone
    );

    //endregion

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
