package com.palyrobotics.frc2018.config;

import com.palyrobotics.frc2018.config.dashboard.DashboardManager;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

public class Gains {
	//Onboard motion profile aka trajectory follower
	public static double kForsetiTrajectorykV = 0.077;
	public static double kForsetiLeftTrajectorykV = 0.0489;
	public static double kForsetiRightTrajectorykV = 0.0499;
	public static double kForsetiLeftTrajectorykV_0 = 0.0969;
	public static double kForsetiRightTrajectorykV_0 = 0.0946;
	public static double kForsetiTrajectorykA = 0.025;

//	public static final double kForsetiDriveVelocitykP = 1.2;//6.0 / 2;
//	public static final double kForsetiDriveVelocitykI = 0.001;
//	public static final double kForsetiDriveVelocitykD = 12.4;//85 / 2;
//	public static final double kForsetiDriveVelocitykF = 0.246537885;//2.624 / 2;
//	public static final int kForsetiDriveVelocitykIzone = 0;//800 / 2;
//	public static final double kForsetiDriveVelocitykRampRate = 0.0;
	public static final double kForsetiDriveVelocitykP = 0.242*1.2;//6.0 / 2;
	public static final double kForsetiDriveVelocitykI = 0.0;//0.001;
    public static final double kForsetiDriveVelocitykD = 11.5*1.2;//12.4;//85 / 2;
    public static final double kForsetiDriveVelocitykF = 0.152807;//0.258987;//0.010516;//2.624 / 2;
	public static final int kForsetiDriveVelocitykIzone = 0;//800 / 2;
	public static final double kForsetiDriveVelocitykRampRate = 0.0;
	public static final Gains forsetiVelocity = new Gains(kForsetiDriveVelocitykP, kForsetiDriveVelocitykI, kForsetiDriveVelocitykD, kForsetiDriveVelocitykF,
			kForsetiDriveVelocitykIzone, kForsetiDriveVelocitykRampRate);

	//Drive Distance PID control loop
	public static final double kForsetiDriveStraightTurnkP = -0.06;
	public static final double kForsetiDriveDistancekP = 0.5;
	public static final double kForsetiDriveDistancekI = 0.0025;
	public static final double kForsetiDriveDistancekD = 12.0;
	public static final int kForsetiDriveDistancekIzone = 125;
	public static final double kForsetiDriveDistancekRampRate = 0.0;
	public static final Gains forsetiDriveDistance = new Gains(kForsetiDriveDistancekP, kForsetiDriveDistancekI, kForsetiDriveDistancekD, 0,
			kForsetiDriveDistancekIzone, kForsetiDriveDistancekRampRate);

	//Drive Motion Magic offboard control loop
	//Short distance max speed 45 in/s Max accel 95 in/s^2
	public static final double kForsetiShortDriveMotionMagicCruiseVelocity = 60 * Constants.kDriveSpeedUnitConversion;
	public static final double kForsetiShortDriveMotionMagicMaxAcceleration = 120 * Constants.kDriveSpeedUnitConversion;
	public static final double kForsetiShortDriveMotionMagickP = .5 ;
	public static final double kForsetiShortDriveMotionMagickI = 0; //0.00040 / 2;
	public static final double kForsetiShortDriveMotionMagickD = 0; //275 / 2;
	public static final double kForsetiShortDriveMotionMagickF = .1821; //2.075 / 2;
	public static final int kForsetiShortDriveMotionMagickIzone = 0; //150 / 2;
	public static final double kForsetiShortDriveMotionMagickRampRate = 0.0;
	public static final Gains forsetiShortDriveMotionMagicGains = new Gains(kForsetiShortDriveMotionMagickP, kForsetiShortDriveMotionMagickI,
			kForsetiShortDriveMotionMagickD, kForsetiShortDriveMotionMagickF, kForsetiShortDriveMotionMagickIzone, kForsetiShortDriveMotionMagickRampRate);

	//Long distance more aggressive, 180 in/s, 120 in/s^2 accel
	public static final double kForsetiLongDriveMotionMagicCruiseVelocity = 200 * Constants.kDriveSpeedUnitConversion;
	public static final double kForsetiLongDriveMotionMagicMaxAcceleration = 125 * Constants.kDriveSpeedUnitConversion;
	public static final double kForsetiLongDriveMotionMagickP = 3.5;
	public static final double kForsetiLongDriveMotionMagickI = 0.01;
	public static final double kForsetiLongDriveMotionMagickD = 75;
	public static final double kForsetiLongDriveMotionMagickF = 2.0;
	public static final int kForsetiLongDriveMotionMagickIzone = 50;
	public static final double kForsetiLongDriveMotionMagickRampRate = 0.0;
	public static final Gains forsetiLongDriveMotionMagicGains = new Gains(kForsetiLongDriveMotionMagickP, kForsetiLongDriveMotionMagickI,
			kForsetiLongDriveMotionMagickD, kForsetiLongDriveMotionMagickF, kForsetiLongDriveMotionMagickIzone, kForsetiLongDriveMotionMagickRampRate);

	//Drive Motion Magic turn angle gains
	public static final double kForsetiTurnMotionMagicCruiseVelocity = 72 * Constants.kDriveSpeedUnitConversion;
	public static final double kForsetiTurnMotionMagicMaxAcceleration = 36 * Constants.kDriveSpeedUnitConversion;
	public static final double kForsetiTurnMotionMagickP = 6.0;
	public static final double kForsetiTurnMotionMagickI = 0.01;
	public static final double kForsetiTurnMotionMagickD = 210;
	public static final double kForsetiTurnMotionMagickF = 2.0;
	public static final int kForsetiTurnMotionMagickIzone = 50;
	public static final double kForsetiTurnMotionMagickRampRate = 0.0;
	public static final Gains forsetiTurnMotionMagicGains = new Gains(kForsetiTurnMotionMagickP, kForsetiTurnMotionMagickI, kForsetiTurnMotionMagickD,
			kForsetiTurnMotionMagickF, kForsetiTurnMotionMagickIzone, kForsetiTurnMotionMagickRampRate);
	
	//Drive cascading turn angle gains
	public static final double kForsetiCascadingTurnkP = 65;
	public static final double kForsetiCascadingTurnkI = 0;
	public static final double kForsetiCascadingTurnkD = 5;
	public static final double kForsetiCascadingTurnIzone = 7.0;

	//Arm Constants 
	public static final double kForsetiArmDownPositionkP = 0.3;
	public static final double kForsetiArmDownPositionkI = 0.0;
	public static final double kForsetiArmDownPositionkD = 50;
	public static final double kForsetiArmDownPositionkF = 0.0;
	public static final int kForsetiArmDownPositionkIzone = 0;
	public static final double kForsetiArmDownPositionkRampRate = 0.0;
	public static final Gains armDownwardsPosition = new Gains(kForsetiArmDownPositionkP, kForsetiArmDownPositionkI, kForsetiArmDownPositionkD,
			kForsetiArmDownPositionkF, kForsetiArmDownPositionkIzone, kForsetiArmDownPositionkRampRate);

	public static final double kForsetiArmPositionkP = 2.0;
	public static final double kForsetiArmPositionkI = 0.0;
	public static final double kForsetiArmPositionkD = 70.0;
	public static final double kForsetiArmPositionkF = 0.0;
	public static final int kForsetiArmPositionkIzone = 0;
	public static final double kForsetiArmPositionkRampRate = 0.0;
	public static final Gains armPosition = new Gains(kForsetiArmPositionkP, kForsetiArmPositionkI, kForsetiArmPositionkD,
			kForsetiArmPositionkF, kForsetiArmPositionkIzone, kForsetiArmPositionkRampRate);

	public static final double kForsetiArmHoldkP = 2.0;//0.1;
	public static final double kForsetiArmHoldkI = 0;//0.002 / 2;
	public static final double kForsetiArmHoldkD = 30.0;//85 / 2;
	public static final double kForsetiArmHoldkF = 0;//2.624 / 2;
	public static final int kForsetiArmHoldkIzone = 0;//800 / 2;
	public static final double kForsetiArmHoldkRampRate = 0.0;
	public static final Gains armHold = new Gains(kForsetiArmHoldkP, kForsetiArmHoldkI, kForsetiArmHoldkD, kForsetiArmHoldkF,
			kForsetiArmHoldkIzone, kForsetiArmHoldkRampRate);

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
