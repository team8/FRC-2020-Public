package com.palyrobotics.frc2019.config;

import java.util.Objects;

public class Gains {
    // On-board velocity-based follower
    // kV = (gear ratio) / (pi * free speed * wheel diameter)
    // kA = (wheel radius * robot mass) / (total number of motors * gear reduction * motor stall torque)
    // kV ~ 1.1 times theoretical, kA ~ 1.4 times theoretical, kS ~ 1.3V = .11
    // presentation has a typo for kA, should be wheel radius because T = Fr

    // Use these for on-board following
    public static double kVidarTrajectorykV = 0.00344; // 1/(in/s)
    private static double kVidarTrajectorykA = 0.00117; // 1/(in/s^2)
    private static double kVidarTrajectorykS = 0.0179;
    private static double kVidarTrajectorykP = 0;
    private static double kVidarTrajectorykD = 0;

    public static final TrajectoryGains vidarTrajectory = new TrajectoryGains(kVidarTrajectorykV, kVidarTrajectorykA, kVidarTrajectorykS, kVidarTrajectorykP, kVidarTrajectorykD);

    // Use these for off-board following
    private static final double kVidarDriveVelocitykP = .01;
    private static final double kVidarDriveVelocitykI = 0;
    private static final double kVidarDriveVelocitykD = .005;
    private static final double kVidarDriveVelocitykF = 0;
    private static final int kVidarDriveVelocitykIzone = 0;
    private static final double kVidarDriveVelocitykRampRate = 0.0;
    public static final Gains vidarVelocity = new Gains(kVidarDriveVelocitykP, kVidarDriveVelocitykI, kVidarDriveVelocitykD, kVidarDriveVelocitykF,
            kVidarDriveVelocitykIzone, kVidarDriveVelocitykRampRate);

    //Drive Distance PID control loop
    public static final double kVidarDriveStraightTurnkP = -0.06;
    private static final double kVidarDriveDistancekP = 0.5;
    private static final double kVidarDriveDistancekI = 0.0025;
    private static final double kVidarDriveDistancekD = 12.0;
    private static final int kVidarDriveDistancekIzone = 125;
    private static final double kVidarDriveDistancekRampRate = 0.0;
    public static final Gains vidarDriveDistance = new Gains(kVidarDriveDistancekP, kVidarDriveDistancekI, kVidarDriveDistancekD, 0,
            kVidarDriveDistancekIzone, kVidarDriveDistancekRampRate);

    //Drive Motion Magic off-board control loop
    //Short distance max speed 45 in/s Max accel 95 in/s^2
    public static final double kVidarShortDriveMotionMagicCruiseVelocity = 60;
    public static final double kVidarShortDriveMotionMagicMaxAcceleration = 120;
    private static final double kVidarShortDriveMotionMagickP = .5;
    private static final double kVidarShortDriveMotionMagickI = 0; //0.00040 / 2;
    private static final double kVidarShortDriveMotionMagickD = 0; //275 / 2;
    private static final double kVidarShortDriveMotionMagickF = .1821; //2.075 / 2;
    private static final int kVidarShortDriveMotionMagickIzone = 0; //150 / 2;
    private static final double kVidarShortDriveMotionMagickRampRate = 0.0;
    public static final Gains vidarShortDriveMotionMagicGains = new Gains(kVidarShortDriveMotionMagickP, kVidarShortDriveMotionMagickI,
            kVidarShortDriveMotionMagickD, kVidarShortDriveMotionMagickF, kVidarShortDriveMotionMagickIzone, kVidarShortDriveMotionMagickRampRate);

    //Drive Motion Magic turn angle gains
    public static final double kVidarTurnMotionMagicCruiseVelocity = 72;
    public static final double kVidarTurnMotionMagicMaxAcceleration = 36;
    private static final double kVidarTurnMotionMagickP = 6.0;
    private static final double kVidarTurnMotionMagickI = 0.01;
    private static final double kVidarTurnMotionMagickD = 210;
    private static final double kVidarTurnMotionMagickF = 2.0;
    private static final int kVidarTurnMotionMagickIzone = 50;
    private static final double kVidarTurnMotionMagickRampRate = 0.0;
    public static final Gains vidarTurnMotionMagicGains = new Gains(kVidarTurnMotionMagickP, kVidarTurnMotionMagickI, kVidarTurnMotionMagickD,
            kVidarTurnMotionMagickF, kVidarTurnMotionMagickIzone, kVidarTurnMotionMagickRampRate);

    //Drive cascading turn angle gains
    public static final double kVidarCascadingTurnkP = 65;
    public static final double kVidarCascadingTurnkI = 0;
    public static final double kVidarCascadingTurnkD = 5;
    public static final double kVidarCascadingTurnIzone = 7.0;

    //Pusher Constants
    //TODO: Find and tune gains
    private static final double kVidarPusherPositionkP = 0.18;
    private static final double kVidarPusherPositionkI = 0.0;
    private static final double kVidarPusherPositionkD = 1;
    private static final double kVidarPusherPositionkF = 0.0;
    private static final int kVidarPusherPositionkIzone = 0;
    private static final double kVidarPusherPositionkRampRate = 0.0;
    public static final Gains pusherPosition = new Gains(kVidarPusherPositionkP, kVidarPusherPositionkI, kVidarPusherPositionkD,
            kVidarPusherPositionkF, kVidarPusherPositionkIzone, kVidarPusherPositionkRampRate);

    private static final double kVidarIntakePositionkP = .27; // .3;
    private static final double kVidarIntakePositionkI = 0.0;
    private static final double kVidarIntakePositionkD = 0.0; // 2.2;
    private static final double kVidarIntakePositionkF = 0.0;
    private static final int kVidarIntakePositionkIzone = 0;
    private static final double kVidarIntakePositionkRampRate = 1.0;
    public static final Gains intakePosition = new Gains(kVidarIntakePositionkP, kVidarIntakePositionkI, kVidarIntakePositionkD, kVidarIntakePositionkF,
            kVidarIntakePositionkIzone, kVidarIntakePositionkRampRate);

    //kF = (gear ratio) / (free speed)
    public static final double
//            kVidarIntakeSmartMotionMaxVelocity = 260.0, // deg/s
//            kVidarIntakeSmartMotionMaxAcceleration = 450.0; // deg/s^2
            kVidarIntakeSmartMotionMaxVelocity = 270, // deg/s
            kVidarIntakeSmartMotionMaxAcceleration = 440; // deg/s^2
    private static final double
            kVidarIntakeSmartMotionP = 0.000015,
            kVidarIntakeSmartMotionI = 0,
            kVidarIntakeSmartMotionD = 0,
            kVidarIntakeSmartMotionF = 0.00335,
            kVidarIntakeSmartMotionRampRate = 0.1;
    private static final int kVidarIntakeSmartMotionIZone = 0;
    public static final Gains intakeSmartMotion = new Gains(
            kVidarIntakeSmartMotionP, kVidarIntakeSmartMotionI, kVidarIntakeSmartMotionD, kVidarIntakeSmartMotionF,
            kVidarIntakeSmartMotionIZone,
            kVidarIntakeSmartMotionRampRate
    );

    //Elevator Gains
    //region unused
//    private static final double kVidarElevatorHoldkP = 2.0;//0.1;
//    private static final double kVidarElevatorHoldkI = 0;//0.002 / 2;
//    private static final double kVidarElevatorHoldkD = 35.0;//85 / 2;
//    private static final double kVidarElevatorHoldkF = 0;//2.624 / 2;
//    private static final int kVidarElevatorHoldkIzone = 0;//800 / 2;
//    private static final double kVidarElevatorHoldkRampRate = 0.0;
//    public static final Gains elevatorHold = new Gains(kVidarElevatorHoldkP, kVidarElevatorHoldkI, kVidarElevatorHoldkD, kVidarElevatorHoldkF,
//            kVidarElevatorHoldkIzone, kVidarElevatorHoldkRampRate);
//
//    private static final double kVidarClimberHoldkP = 0;
//    private static final double kVidarClimberHoldkI = 0;
//    private static final double kVidarClimberHoldkD = 0;
//    private static final double kVidarClimberHoldkF = 0;
//    private static final int kVidarClimberHoldkIzone = 0;
//    private static final double kVidarClimberHoldkRampRate = 0;
//    public static final Gains climberHold = new Gains(kVidarClimberHoldkP, kVidarClimberHoldkI, kVidarClimberHoldkD, kVidarClimberHoldkF,
//            kVidarClimberHoldkIzone, kVidarClimberHoldkRampRate);
//
//    private static final double kVidarClimberPositionkP = 0;
//    private static final double kVidarClimberPositionkI = 0;
//    private static final double kVidarClimberPositionkD = 0;
//    private static final double kVidarClimberPositionkF = 0;
//    private static final int kVidarClimberPositionkIzone = 0;
//    private static final double kVidarClimberPositionkRampRate = 0;
//    public static final Gains climberPosition = new Gains(kVidarClimberPositionkP, kVidarClimberPositionkI, kVidarClimberPositionkD,
//            kVidarClimberPositionkF, kVidarClimberPositionkIzone, kVidarClimberPositionkRampRate);
//
//    private static final double kVidarElevatorDownPositionkP = 0.3;
//    private static final double kVidarElevatorDownPositionkI = 0.0;
//    private static final double kVidarElevatorDownPositionkD = 58;
//    private static final double kVidarElevatorDownPositionkF = 0.0;
//    private static final int kVidarElevatorDownPositionkIzone = 0;
//    private static final double kVidarElevatorDownPositionkRampRate = 0.0;
//    public static final Gains elevatorDownwardsPosition = new Gains(kVidarElevatorDownPositionkP, kVidarElevatorDownPositionkI, kVidarElevatorDownPositionkD,
//            kVidarElevatorDownPositionkF, kVidarElevatorDownPositionkIzone, kVidarElevatorDownPositionkRampRate);
    //endregion

    private static final double kVidarElevatorPositionP = .5; // .7;
    private static final double kVidarElevatorPositionI = 0.0;
    private static final double kVidarElevatorPositionD = 3.4; // 2.0;
    private static final double kVidarElevatorPositionF = 0.0;
    private static final int kVidarElevatorPositionIZone = 0;
    private static final double kVidarElevatorPositionRampRate = 0.0;
    public static final Gains elevatorPosition = new Gains(
            kVidarElevatorPositionP, kVidarElevatorPositionI, kVidarElevatorPositionD, kVidarElevatorPositionF,
            kVidarElevatorPositionIZone, kVidarElevatorPositionRampRate
    );

//    public static final double
//            kVidarElevatorSmartMotionMaxVelocity = 58,
//            kVidarElevatorSmartMotionMaxAcceleration = 63;
//    public static final Gains elevatorSmartMotion = new Gains(
//            0.0, 0, 0, 0.012,
//            0,
//            0.1);


    public static final Gains emptyGains = new Gains(0, 0, 0, 0, 0, 0);

    public static class TrajectoryGains {
        final public double v, a, s, p, i, d, turnP, turnD;

        public TrajectoryGains(double v, double a, double s, double p, double i, double d, double turnP, double turnD) {
            this.v = v;
            this.a = a;
            this.s = s;
            this.p = p;
            this.i = i;
            this.d = d;
            this.turnP = turnP;
            this.turnD = turnD;
        }

        TrajectoryGains(double v, double a, double s, double p, double d) {
            this.v = v;
            this.a = a;
            this.s = s;
            this.p = p;
            this.i = 0;
            this.d = d;
            this.turnP = 0;
            this.turnD = 0;
        }
    }

    public final double P, I, D, F, rampRate;
    public final int iZone; // TODO I Zone is double on sparks, not integer like Talon/Victor

    public Gains(double p, double i, double d, double f, int iZone, double rampRate) {
        this.P = p;
        this.I = i;
        this.D = d;
        this.F = f;
        this.iZone = iZone;
        this.rampRate = rampRate;
    }

    /**
     * Auto generated by IntelliJ
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Gains gains = (Gains) other;
        return Double.compare(gains.P, P) == 0 &&
                Double.compare(gains.I, I) == 0 &&
                Double.compare(gains.D, D) == 0 &&
                Double.compare(gains.F, F) == 0 &&
                Double.compare(gains.rampRate, rampRate) == 0 &&
                iZone == gains.iZone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(P, I, D, F, rampRate, iZone);
    }

    @Override
    public String toString() {
        return String.format("Gains{P=%s, I=%s, D=%s, F=%s, rampRate=%s, iZone=%d}", P, I, D, F, rampRate, iZone);
    }
}
