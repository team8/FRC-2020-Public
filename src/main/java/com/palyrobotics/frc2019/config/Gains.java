package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;

public class Gains {
	//Onboard motion profile aka trajectory follower
	public static double kVidarTrajectorykV = 0.077;
	public static double kVidarLeftTrajectorykV = 0.0489;
	public static double kVidarRightTrajectorykV = 0.0499;
	public static double kVidarLeftTrajectorykV_0 = 0.0969;
	public static double kVidarRightTrajectorykV_0 = 0.0946;
	public static double kVidarTrajectorykA = 0.025;

//	public static final double kVidarDriveVelocitykP = 1.2;//6.0 / 2;
//	public static final double kVidarDriveVelocitykI = 0.001;
//	public static final double kVidarDriveVelocitykD = 12.4;//85 / 2;
//	public static final double kVidarDriveVelocitykF = 0.246537885;//2.624 / 2;
//	public static final int kVidarDriveVelocitykIzone = 0;//800 / 2;
//	public static final double kVidarDriveVelocitykRampRate = 0.0;
	public static final double kVidarDriveVelocitykP = 0.242*1.2;//6.0 / 2;
	public static final double kVidarDriveVelocitykI = 0.0;//0.001;
    public static final double kVidarDriveVelocitykD = 11.5*1.2;//12.4;//85 / 2;
    public static final double kVidarDriveVelocitykF = 0.152807;//0.258987;//0.010516;//2.624 / 2;
	public static final int kVidarDriveVelocitykIzone = 0;//800 / 2;
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
	public static final double kVidarShortDriveMotionMagicCruiseVelocity = 60 * Constants.kDriveSpeedUnitConversion;
	public static final double kVidarShortDriveMotionMagicMaxAcceleration = 120 * Constants.kDriveSpeedUnitConversion;
	public static final double kVidarShortDriveMotionMagickP = .5 ;
	public static final double kVidarShortDriveMotionMagickI = 0; //0.00040 / 2;
	public static final double kVidarShortDriveMotionMagickD = 0; //275 / 2;
	public static final double kVidarShortDriveMotionMagickF = .1821; //2.075 / 2;
	public static final int kVidarShortDriveMotionMagickIzone = 0; //150 / 2;
	public static final double kVidarShortDriveMotionMagickRampRate = 0.0;
	public static final Gains vidarShortDriveMotionMagicGains = new Gains(kVidarShortDriveMotionMagickP, kVidarShortDriveMotionMagickI,
			kVidarShortDriveMotionMagickD, kVidarShortDriveMotionMagickF, kVidarShortDriveMotionMagickIzone, kVidarShortDriveMotionMagickRampRate);

	//Drive Motion Magic turn angle gains
	public static final double kVidarTurnMotionMagicCruiseVelocity = 72 * Constants.kDriveSpeedUnitConversion;
	public static final double kVidarTurnMotionMagicMaxAcceleration = 36 * Constants.kDriveSpeedUnitConversion;
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

	//Elevator Gains
    public static final double kForsetiElevatorHoldkP = 2.0;//0.1;
    public static final double kForsetiElevatorHoldkI = 0;//0.002 / 2;
    public static final double kForsetiElevatorHoldkD = 30.0;//85 / 2;
    public static final double kForsetiElevatorHoldkF = 0;//2.624 / 2;
    public static final int kForsetiElevatorHoldkIzone = 0;//800 / 2;
    public static final double kForsetiElevatorHoldkRampRate = 0.0;
    public static final Gains elevatorHold = new Gains(kForsetiElevatorHoldkP, kForsetiElevatorHoldkI, kForsetiElevatorHoldkD, kForsetiElevatorHoldkF,
            kForsetiElevatorHoldkIzone, kForsetiElevatorHoldkRampRate);

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

    public static final double kForsetiElevatorDownPositionkP = 0.3;
    public static final double kForsetiElevatorDownPositionkI = 0.0;
    public static final double kForsetiElevatorDownPositionkD = 50;
    public static final double kForsetiElevatorDownPositionkF = 0.0;
    public static final int kForsetiElevatorDownPositionkIzone = 0;
    public static final double kForsetiElevatorDownPositionkRampRate = 0.0;
    public static final Gains elevatorDownwardsPosition = new Gains(kForsetiElevatorDownPositionkP, kForsetiElevatorDownPositionkI, kForsetiElevatorDownPositionkD,
            kForsetiElevatorDownPositionkF, kForsetiElevatorDownPositionkIzone, kForsetiElevatorDownPositionkRampRate);

    public static final double kForsetiElevatorPositionkP = 2.0;
    public static final double kForsetiElevatorPositionkI = 0.0;
    public static final double kForsetiElevatorPositionkD = 70.0;
    public static final double kForsetiElevatorPositionkF = 0.0;
    public static final int kForsetiElevatorPositionkIzone = 0;
    public static final double kForsetiElevatorPositionkRampRate = 0.0;
    public static final Gains elevatorPosition = new Gains(kForsetiElevatorPositionkP, kForsetiElevatorPositionkI, kForsetiElevatorPositionkD,
            kForsetiElevatorPositionkF, kForsetiElevatorPositionkIzone, kForsetiElevatorPositionkRampRate);

	//Arm Constants 
	public static final double kVidarArmDownPositionkP = 0.3;
	public static final double kVidarArmDownPositionkI = 0.0;
	public static final double kVidarArmDownPositionkD = 50;
	public static final double kVidarArmDownPositionkF = 0.0;
	public static final int kVidarArmDownPositionkIzone = 0;
	public static final double kVidarArmDownPositionkRampRate = 0.0;
	public static final Gains armDownwardsPosition = new Gains(kVidarArmDownPositionkP, kVidarArmDownPositionkI, kVidarArmDownPositionkD,
			kVidarArmDownPositionkF, kVidarArmDownPositionkIzone, kVidarArmDownPositionkRampRate);

	public static final double kVidarArmPositionkP = 2.0;
	public static final double kVidarArmPositionkI = 0.0;
	public static final double kVidarArmPositionkD = 70.0;
	public static final double kVidarArmPositionkF = 0.0;
	public static final int kVidarArmPositionkIzone = 0;
	public static final double kVidarArmPositionkRampRate = 0.0;
	public static final Gains armPosition = new Gains(kVidarArmPositionkP, kVidarArmPositionkI, kVidarArmPositionkD,
			kVidarArmPositionkF, kVidarArmPositionkIzone, kVidarArmPositionkRampRate);

	public static final double kVidarArmHoldkP = 2.0;//0.1;
	public static final double kVidarArmHoldkI = 0;//0.002 / 2;
	public static final double kVidarArmHoldkD = 30.0;//85 / 2;
	public static final double kVidarArmHoldkF = 0;//2.624 / 2;
	public static final int kVidarArmHoldkIzone = 0;//800 / 2;
	public static final double kVidarArmHoldkRampRate = 0.0;
	public static final Gains armHold = new Gains(kVidarArmHoldkP, kVidarArmHoldkI, kVidarArmHoldkD, kVidarArmHoldkF,
			kVidarArmHoldkIzone, kVidarArmHoldkRampRate);

	public static class TrajectoryGains {
		public final double P, D, V, A, turnP, turnD;

		public TrajectoryGains(double p, double d, double v, double a, double turnP, double turnD) {
			this.P = p;
			this.D = d;
			this.V = v;
			this.A = a;
			this.turnP = turnP;
			this.turnD = turnD;
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
