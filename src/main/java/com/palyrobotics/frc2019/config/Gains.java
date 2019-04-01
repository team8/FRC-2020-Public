package com.palyrobotics.frc2019.config;

import java.util.logging.Level;

import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.util.logger.Logger;

public class Gains {
	//Onboard velocity-based follower
	//kV = (gear ratio) / (pi * free speed * wheel diameter)
	//kA = (wheel radius * robot mass) / (total number of motors * gear reduction * motor stall torque)
	//kV ~ 1.1 times theoretical, kA ~ 1.4 times theroretical, kS ~ 1.3V = .11
	//presentation has a typo for kA, should be wheel radius because T = Fr

	//Use these for onboard following
	public static double kVidarTrajectorykV = 0.00344; // 1/(in/s)
	public static double kVidarTrajectorykA = 0.00117; // 1/(in/s^2)
	public static double kVidarTrajectorykS = 0.0179;
	public static double kVidarTrajectorykP = 0;
	public static double kVidarTrajectorykD = 0;

	public static final TrajectoryGains vidarTrajectory = new TrajectoryGains(kVidarTrajectorykV, kVidarTrajectorykA, kVidarTrajectorykS, kVidarTrajectorykP, kVidarTrajectorykD);

	//Use these for offboard following
	public static final double kVidarDriveVelocitykP = .01;
	public static final double kVidarDriveVelocitykI = 0;
    public static final double kVidarDriveVelocitykD = .005;
    public static final double kVidarDriveVelocitykF = 0;
	public static final int kVidarDriveVelocitykIzone = 0;
	public static final double kVidarDriveVelocitykRampRate = 0.0;
	public static final Gains vidarVelocity = new Gains(kVidarDriveVelocitykP, kVidarDriveVelocitykI, kVidarDriveVelocitykD, kVidarDriveVelocitykF,
			kVidarDriveVelocitykIzone, kVidarDriveVelocitykRampRate);

	//Drive Distance PID control loop
	public static final double kVidarDriveStraightTurnkP = -0.06;
	public static final double kVidarDriveDistancekP = 0.5;
	public static final double kVidarDriveDistancekI = 0.0025;
	public static final double kVidarDriveDistancekD = 12.0;
	public static final int kVidarDriveDistancekIzone = 125;
	public static final double kVidarDriveDistancekRampRate = 0.0;
	public static final Gains vidarDriveDistance = new Gains(kVidarDriveDistancekP, kVidarDriveDistancekI, kVidarDriveDistancekD, 0,
			kVidarDriveDistancekIzone, kVidarDriveDistancekRampRate);

	//Drive Motion Magic offboard control loop
	//Short distance max speed 45 in/s Max accel 95 in/s^2
	public static final double kVidarShortDriveMotionMagicCruiseVelocity = 60;
	public static final double kVidarShortDriveMotionMagicMaxAcceleration = 120;
	public static final double kVidarShortDriveMotionMagickP = .5 ;
	public static final double kVidarShortDriveMotionMagickI = 0; //0.00040 / 2;
	public static final double kVidarShortDriveMotionMagickD = 0; //275 / 2;
	public static final double kVidarShortDriveMotionMagickF = .1821; //2.075 / 2;
	public static final int kVidarShortDriveMotionMagickIzone = 0; //150 / 2;
	public static final double kVidarShortDriveMotionMagickRampRate = 0.0;
	public static final Gains vidarShortDriveMotionMagicGains = new Gains(kVidarShortDriveMotionMagickP, kVidarShortDriveMotionMagickI,
			kVidarShortDriveMotionMagickD, kVidarShortDriveMotionMagickF, kVidarShortDriveMotionMagickIzone, kVidarShortDriveMotionMagickRampRate);

	//Drive Motion Magic turn angle gains
	public static final double kVidarTurnMotionMagicCruiseVelocity = 72;
	public static final double kVidarTurnMotionMagicMaxAcceleration = 36;
	public static final double kVidarTurnMotionMagickP = 6.0;
	public static final double kVidarTurnMotionMagickI = 0.01;
	public static final double kVidarTurnMotionMagickD = 210;
	public static final double kVidarTurnMotionMagickF = 2.0;
	public static final int kVidarTurnMotionMagickIzone = 50;
	public static final double kVidarTurnMotionMagickRampRate = 0.0;
	public static final Gains vidarTurnMotionMagicGains = new Gains(kVidarTurnMotionMagickP, kVidarTurnMotionMagickI, kVidarTurnMotionMagickD,
			kVidarTurnMotionMagickF, kVidarTurnMotionMagickIzone, kVidarTurnMotionMagickRampRate);
	
	//Drive cascading turn angle gains
	public static final double kVidarCascadingTurnkP = 65;
	public static final double kVidarCascadingTurnkI = 0;
	public static final double kVidarCascadingTurnkD = 5;
	public static final double kVidarCascadingTurnIzone = 7.0;

	//Pusher Constants
    //TODO: Find and tune gains
    public static final double kVidarPusherPositionkP = 0.18;
    public static final double kVidarPusherPositionkI = 0.0;
    public static final double kVidarPusherPositionkD = 1;
    public static final double kVidarPusherPositionkF = 0.0;
    public static final int kVidarPusherPositionkIzone = 0;
    public static final double kVidarPusherPositionkRampRate = 0.0;
    public static final Gains pusherPosition = new Gains(kVidarPusherPositionkP, kVidarPusherPositionkI, kVidarPusherPositionkD,
            kVidarPusherPositionkF, kVidarPusherPositionkIzone, kVidarPusherPositionkRampRate);

	public static final double kVidarIntakePositionkP = .27; // .3;
	public static final double kVidarIntakePositionkI = 0.0;
	public static final double kVidarIntakePositionkD = 0.0; // 2.2;
	public static final double kVidarIntakePositionkF = 0.0;
	public static final int kVidarIntakePositionkIzone = 0;
	public static final double kVidarIntakePositionkRampRate = 1.0;
	public static final Gains intakePosition = new Gains(kVidarIntakePositionkP, kVidarIntakePositionkI, kVidarIntakePositionkD, kVidarIntakePositionkF,
			kVidarIntakePositionkIzone, kVidarIntakePositionkRampRate);

	//kF = (gear ratio) / (free speed)
	public static final double kVidarIntakeSmartMotionMaxVel = 100; // deg/s
	public static final double kVidarIntakeSmartMotionMaxAccel = 200; // deg/s^2
	public static final double kVidarIntakeSmartMotionkP = 0;
	public static final double kVidarIntakeSmartMotionkI = 0;
	public static final double kVidarIntakeSmartMotionkD = 0;
	public static final double kVidarIntakeSmartMotionkF = 0.00346; // 1/(deg/s)
	public static final int kVidarIntakeSmartMotionkIzone = 0;
	public static final double kVidarIntakeSmartMotionkRampRate = 0;
	public static final Gains intakeSmartMotion = new Gains(kVidarIntakeSmartMotionkP, kVidarIntakeSmartMotionkI, kVidarIntakeSmartMotionkD, kVidarIntakeSmartMotionkF,
	kVidarIntakeSmartMotionkIzone, kVidarIntakeSmartMotionkRampRate);

	//Elevator Gains
    public static final double kVidarElevatorHoldkP = 2.0;//0.1;
    public static final double kVidarElevatorHoldkI = 0;//0.002 / 2;
    public static final double kVidarElevatorHoldkD = 35.0;//85 / 2;
    public static final double kVidarElevatorHoldkF = 0;//2.624 / 2;
    public static final int kVidarElevatorHoldkIzone = 0;//800 / 2;
    public static final double kVidarElevatorHoldkRampRate = 0.0;
    public static final Gains elevatorHold = new Gains(kVidarElevatorHoldkP, kVidarElevatorHoldkI, kVidarElevatorHoldkD, kVidarElevatorHoldkF,
            kVidarElevatorHoldkIzone, kVidarElevatorHoldkRampRate);

    public static final double kVidarClimberHoldkP = 0;
    public static final double kVidarClimberHoldkI = 0;
    public static final double kVidarClimberHoldkD = 0;
    public static final double kVidarClimberHoldkF = 0;
    public static final int kVidarClimberHoldkIzone = 0;
    public static final double kVidarClimberHoldkRampRate = 0;
    public static final Gains climberHold = new Gains(kVidarClimberHoldkP, kVidarClimberHoldkI, kVidarClimberHoldkD, kVidarClimberHoldkF,
            kVidarClimberHoldkIzone, kVidarClimberHoldkRampRate);

    public static final double kVidarClimberPositionkP = 0;
    public static final double kVidarClimberPositionkI = 0;
    public static final double kVidarClimberPositionkD = 0;
    public static final double kVidarClimberPositionkF = 0;
    public static final int kVidarClimberPositionkIzone = 0;
    public static final double kVidarClimberPositionkRampRate = 0;
    public static final Gains climberPosition = new Gains(kVidarClimberPositionkP, kVidarClimberPositionkI, kVidarClimberPositionkD,
            kVidarClimberPositionkF, kVidarClimberPositionkIzone, kVidarClimberPositionkRampRate);

    public static final double kVidarElevatorDownPositionkP = 0.3;
    public static final double kVidarElevatorDownPositionkI = 0.0;
    public static final double kVidarElevatorDownPositionkD = 58;
    public static final double kVidarElevatorDownPositionkF = 0.0;
    public static final int kVidarElevatorDownPositionkIzone = 0;
    public static final double kVidarElevatorDownPositionkRampRate = 0.0;
    public static final Gains elevatorDownwardsPosition = new Gains(kVidarElevatorDownPositionkP, kVidarElevatorDownPositionkI, kVidarElevatorDownPositionkD,
            kVidarElevatorDownPositionkF, kVidarElevatorDownPositionkIzone, kVidarElevatorDownPositionkRampRate);

    public static final double kVidarElevatorPositionkP = .5; // .7;
    public static final double kVidarElevatorPositionkI = 0.0;
    public static final double kVidarElevatorPositionkD = 3.4; // 2.0;
    public static final double kVidarElevatorPositionkF = 0.0;
    public static final int kVidarElevatorPositionkIzone = 0;
    public static final double kVidarElevatorPositionkRampRate = 0.0;
    public static final Gains elevatorPosition = new Gains(kVidarElevatorPositionkP, kVidarElevatorPositionkI, kVidarElevatorPositionkD,
            kVidarElevatorPositionkF, kVidarElevatorPositionkIzone, kVidarElevatorPositionkRampRate);


    public static final Gains emptyGains = new Gains(0,0,0,0,0,0);

	public static class TrajectoryGains {
		public final double v, a, s, p, i, d, turnP, turnD;

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

		public TrajectoryGains(double v, double a, double s, double p, double d) {
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
	public final int izone;

	public Gains(double p, double i, double d, double f, int izone, double rampRate) {
		this.P = p;
		this.I = i;
		this.D = d;
		this.F = f;
		this.izone = izone;
		this.rampRate = rampRate;
	}

	@Override
	public boolean equals(Object other) {
		return ((Gains) other).P == this.P && ((Gains) other).I == this.I && ((Gains) other).D == this.D && ((Gains) other).F == this.F
				&& ((Gains) other).izone == this.izone && ((Gains) other).rampRate == this.rampRate;
	}

	public static void initNetworkTableGains() {
		if(DashboardManager.getInstance().pidTuning) {
			Logger.getInstance().logRobotThread(Level.INFO, "Dashboard tuning currently removed");
		}
	}

	public static void updateNetworkTableGains() {
		if(DashboardManager.getInstance().pidTuning) {
			Logger.getInstance().logRobotThread(Level.INFO, "Dashboard tuning currently removed");
		}
	}
}
