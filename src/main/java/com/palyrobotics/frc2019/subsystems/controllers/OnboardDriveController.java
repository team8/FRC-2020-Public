package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Gains.TrajectoryGains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.revrobotics.ControlType;

/**
 * Used to run onboard position or velocity control of the drivetrain
 * This controller is intended to be used in a cascading manner, with a parent controller that generates realtime setpoints
 * 
 */
public class OnboardDriveController implements Drive.DriveController {

	private SparkSignal mSignal;
	private RobotState mCachedState;
	private OnboardControlType controlType; 
	private TrajectoryGains mGains;

	private Segment leftSetpoint;
	private Segment rightSetpoint;

	private double left_last_error = 0;
	private double right_last_error = 0;

	public enum OnboardControlType {
		kPosition,
		kVelocity;
	}
	
	public OnboardDriveController(OnboardControlType controlType, TrajectoryGains gains) {
		//Use copy constructors and prevent the signal passed in from being modified externally
		this.mSignal = SparkSignal.getNeutralSignal();
		this.leftSetpoint = new Segment();
		this.rightSetpoint = new Segment();
		this.mGains = gains;
		this.controlType = controlType;
	}

	@Override
	public SparkSignal update(RobotState state) {
		mCachedState = state;
		switch (this.controlType) {
			case kPosition:
				this.mSignal = getPositionSetpoint(state.drivePose);
				break;
			case kVelocity:
				this.mSignal = getVelocitySetpoint(state.drivePose);
				break;
			default:
				this.mSignal = SparkSignal.getNeutralSignal();
				break;
		}

		return this.mSignal;
	}

	/**
	 * Should only be called by a parent controller
	 */
	public void updateSetpoint(Segment leftSetpoint, Segment rightSetpoint, Object handle) throws IllegalAccessException {
		if (!(handle instanceof Drive.DriveController)) {
			throw new IllegalAccessException();
		}
		this.leftSetpoint = leftSetpoint;
		this.rightSetpoint = rightSetpoint;
	}

	private SparkSignal getPositionSetpoint(Pose drivePose) {
		double left_sp = leftSetpoint.pos;
		double left_pv = drivePose.leftEnc * DrivetrainConstants.kDriveInchesPerRotation;
		double right_sp = rightSetpoint.pos;
		double right_pv = drivePose.rightEnc * DrivetrainConstants.kDriveSpeedUnitConversion;

		return updatePID(left_sp, left_pv, right_sp, right_pv);
	}

	private SparkSignal getVelocitySetpoint(Pose drivePose) {
		double left_sp = leftSetpoint.vel;
		double left_pv = drivePose.leftEncVelocity * DrivetrainConstants.kDriveSpeedUnitConversion;
		double right_sp = rightSetpoint.vel;
		double right_pv = drivePose.rightEncVelocity * DrivetrainConstants.kDriveSpeedUnitConversion;

		return updatePID(left_sp, left_pv, right_sp, right_pv);
	}

	/**
	 * sp = setpoint, goal value
	 * pv = process variable, actual value
	 */
	private SparkSignal updatePID(double left_sp, double left_pv, double right_sp, double right_pv) {

		//calculate error
		double left_error = left_sp - left_pv;
		double right_error = right_sp - right_pv;

		//calculate d_error
		double d_left_error = (left_error - left_last_error) / leftSetpoint.dt;
		double d_right_error = (right_error - right_last_error) / rightSetpoint.dt;

		//calculate output
		//output = kP * error + kD * d_error + kV * velocity + kA * accel + sign(velocity)*kS
		double left_output = mGains.p * left_error + mGains.d * d_left_error + mGains.v * leftSetpoint.vel + mGains.a * leftSetpoint.acc + Math.signum(leftSetpoint.vel)*mGains.s;
		double right_output = mGains.p * right_error + mGains.d * d_right_error + mGains.v * rightSetpoint.vel + mGains.a * rightSetpoint.acc + Math.signum(rightSetpoint.vel)*mGains.s;

		//clamp output within [-1,1] because we don't have infinite power
		left_output = Math.min(Math.max(left_output, -1), 1);
		right_output = Math.min(Math.max(right_output, -1), 1);

		//deadband output within (-0.03, 0.03) to avoid jitter with kS
		left_output = Math.abs(left_output) < 0.03 ? 0 : left_output;
		right_output = Math.abs(right_output) < 0.03 ? 0 : right_output;

		left_last_error = left_error;
		right_last_error = right_error;

		return new SparkSignal(new SparkMaxOutput(null, ControlType.kVoltage, left_output), new SparkMaxOutput(null, ControlType.kVoltage, right_output));
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public boolean onTarget() {
		if (mCachedState == null) {
			return false;
		}
		//unimplemented, not used with cascading control
		return true;
	}

	/** 
	 * Helper class to represent a setpoint
	 */
	public static class Segment {

		public double pos, vel, acc, jerk, heading, dt, x, y;
	
		public Segment() {
		}

		public Segment(double vel, double acc, double dt) {
			this(0, vel, acc, 0, 0, dt, 0, 0);
		  }

		public Segment(double pos, double vel, double acc, double dt) {
			this(pos, vel, acc, 0, 0, dt, 0, 0);
		}
	
		public Segment(double pos, double vel, double acc, double jerk,
				double heading, double dt, double x, double y) {
		  this.pos = pos;
		  this.vel = vel;
		  this.acc = acc;
		  this.jerk = jerk;
		  this.heading = heading;
		  this.dt = dt;
		  this.x = x;
		  this.y = y;
		}
	
		public Segment(Segment to_copy) {
		  pos = to_copy.pos;
		  vel = to_copy.vel;
		  acc = to_copy.acc;
		  jerk = to_copy.jerk;
		  heading = to_copy.heading;
		  dt = to_copy.dt;
		  x = to_copy.x;
		  y = to_copy.y;
		}
	
		public String toString() {
		  return "pos: " + pos + "; vel: " + vel + "; acc: " + acc + "; jerk: "
				  + jerk + "; heading: " + heading;
		}
	}


}