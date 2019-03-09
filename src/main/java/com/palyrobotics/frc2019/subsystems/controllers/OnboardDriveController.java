package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.Gains.TrajectoryGains;
import com.palyrobotics.frc2019.config.RobotState;
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

	private TrajectorySegment leftSetpoint;
	private TrajectorySegment rightSetpoint;

	private double left_last_error = 0;
	private double right_last_error = 0;

	private boolean reset = true;

	public enum OnboardControlType {
		kPosition,
		kVelocity,
		kVelArbFF;
	}
	
	public OnboardDriveController(OnboardControlType controlType, TrajectoryGains gains) {
		//Use copy constructors and prevent the signal passed in from being modified externally
		this.mSignal = SparkSignal.getNeutralSignal();
		this.leftSetpoint = new TrajectorySegment();
		this.rightSetpoint = new TrajectorySegment();
		this.mGains = gains;
		this.controlType = controlType;
	}

	@Override
	public SparkSignal update(RobotState state) {
		mCachedState = state;
		switch (this.controlType) {
			case kPosition:
				this.mSignal = getPositionOutput(state.drivePose);
				break;
			case kVelocity:
				this.mSignal = getVelocityOutput(state.drivePose);
				break;
			case kVelArbFF:
				this.mSignal = getArbFFOutput(state.drivePose);
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
	public void updateSetpoint(TrajectorySegment leftSetpoint, TrajectorySegment rightSetpoint, Object handle) throws IllegalAccessException {
		if (!(handle instanceof Drive.DriveController)) {
			throw new IllegalAccessException();
		}
		this.leftSetpoint = leftSetpoint;
		this.rightSetpoint = rightSetpoint;
	}

	private SparkSignal getPositionOutput(Pose drivePose) {
		double left_sp = leftSetpoint.pos;
		double left_pv = drivePose.leftEnc;
		double right_sp = rightSetpoint.pos;
		double right_pv = drivePose.rightEnc;

		return updatePID(left_sp, left_pv, right_sp, right_pv);
	}

	private SparkSignal getVelocityOutput(Pose drivePose) {
		double left_sp = leftSetpoint.vel;
		double left_pv = drivePose.leftEncVelocity;
		double right_sp = rightSetpoint.vel;
		double right_pv = drivePose.rightEncVelocity;

		return updatePID(left_sp, left_pv, right_sp, right_pv);
	}

	private SparkSignal getArbFFOutput(Pose drivePose) {
		SparkSignal signal = updatePID(0, 0, 0, 0);
		signal.leftMotor.setTargetVelocity(leftSetpoint.vel, signal.leftMotor.getSetpoint()*12.0, new Gains(mGains.p, 0, mGains.d, 0, 0, 0));
		signal.rightMotor.setTargetVelocity(rightSetpoint.vel, signal.rightMotor.getSetpoint()*12.0, new Gains(mGains.p, 0, mGains.d, 0, 0, 0));
		
		return signal;
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

		//ignore d_error on the first cycle
		if (reset) {
			d_left_error = 0;
			d_right_error = 0;
			reset = false;
		}

		//calculate output
		//output = kP * error + kD * d_error + kV * velocity + kA * accel + sign(velocity)*kS
		double left_output = mGains.p * left_error + mGains.d * d_left_error + mGains.v * leftSetpoint.vel + mGains.a * leftSetpoint.acc + Math.signum(leftSetpoint.vel)*mGains.s;
		double right_output = mGains.p * right_error + mGains.d * d_right_error + mGains.v * rightSetpoint.vel + mGains.a * rightSetpoint.acc + Math.signum(rightSetpoint.vel)*mGains.s;

		//clamp output within [-1,1] because we don't have infinite power
		left_output = Math.min(Math.max(left_output, -1), 1);
		right_output = Math.min(Math.max(right_output, -1), 1);

		//deadband output within (-kS-0.02, kS+0.02) to avoid jitter with kS
		left_output = Math.abs(left_output) - mGains.s < 0.02 ? 0 : left_output;
		right_output = Math.abs(right_output) - mGains.s < 0.02 ? 0 : right_output;

		left_last_error = left_error;
		right_last_error = right_error;

		return new SparkSignal(new SparkMaxOutput(null, ControlType.kDutyCycle, left_output), new SparkMaxOutput(null, ControlType.kDutyCycle, right_output));
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
	public static class TrajectorySegment {

		public double pos, vel, acc, dt;
	
		public TrajectorySegment() {
		}

		public TrajectorySegment(double vel, double acc, double dt) {
			this(0, vel, acc, dt);
		  }

		public TrajectorySegment(double pos, double vel, double acc, double dt) {
			this.pos = pos;
			this.vel = vel;
			this.acc = acc;
			this.dt = dt;
		}
	
		public TrajectorySegment(TrajectorySegment to_copy) {
		  pos = to_copy.pos;
		  vel = to_copy.vel;
		  acc = to_copy.acc;
		  dt = to_copy.dt;
		}
	
		public String toString() {
		  return "pos: " + pos + "; vel: " + vel + "; acc: " + acc + "; dt: " + dt;
		}
	}


}