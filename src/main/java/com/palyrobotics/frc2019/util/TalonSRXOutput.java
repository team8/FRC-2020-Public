package com.palyrobotics.frc2018.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2018.config.Gains;

/**
 * Created by Nihar on 1/14/17. Mocks the output of a CANTalon's configuration Allows passthrough of -1 to 1 mSignal Allows configuration for offboard SRX
 * calculations
 * 
 * @author Nihar
 */
public class TalonSRXOutput {

	/**
	 * Prevent null pointer exceptions
	 */
	private ControlMode controlMode;

	//Velocity, Position, MotionMagic, PercentOutput, Current, Disabled, MotionProfile
	private double setpoint; //Encoder ticks
	private double arbitraryDemand; // Arbitrary demand used in the PIDF + PID[1]
	public int profile;
	public Gains gains;

	//Used for motion magic
	public int accel;
	public int cruiseVel;

	/**
	 * Default constructor
	 */
	public TalonSRXOutput() {
		controlMode = ControlMode.Disabled;
		setpoint = 0;
		profile = 0;
		gains = new Gains(0, 0, 0, 0, 0, 0);

		accel = 0;
		cruiseVel = 0;
	}

	/**
	 * Copy constructor
	 * 
	 * @param talon
	 *            output to copy
	 */
	public TalonSRXOutput(TalonSRXOutput talon) {
		this.controlMode = talon.getControlMode();
		this.setpoint = talon.getSetpoint();
		this.profile = talon.profile;
		this.gains = talon.gains;

		this.accel = talon.accel;
		this.cruiseVel = talon.cruiseVel;
		this.profile = talon.profile;
	}

	public TalonSRXOutput(ControlMode controlMode, Gains gains, double setpoint) {
		this.controlMode = controlMode;
		this.setpoint = setpoint;
		profile = 0;
		this.gains = gains;

		accel = 0;
		cruiseVel = 0;
	}

	public ControlMode getControlMode() {
		return controlMode;
	}

	public double getSetpoint() {
		return setpoint;
	}
	public double getArbitraryFF() {
		return arbitraryDemand;
	}

	/**
	 * Sets Talon to ControlMode.Velocity, velocity target control loop
	 * 
	 * @param speed,
	 *            target velocity (from -1023019 to 10230?)
	 */
	public void setVelocity(double speed, Gains gains) {
		controlMode = ControlMode.Velocity;
		setpoint = speed;
		this.gains = gains;
	}

	/**
	 * Sets Talon to ControlMode.Position
	 * 
	 * @param setpoint,
	 *            target distance in native units
	 */
	public void setPosition(double setpoint, Gains gains) {
		controlMode = ControlMode.Position;
		this.setpoint = setpoint;
		this.gains = gains;
		this.arbitraryDemand = 0.0;
	}


	/**
	 * Sets Talon to ControlMode.Position
	 *
	 * @param setpoint,
	 *            target distance in native units
	 */
	public void setPosition(double setpoint, Gains gains, double arbitraryDemand) {
		controlMode = ControlMode.Position;
		this.setpoint = setpoint;
		this.gains = gains;
		this.arbitraryDemand = arbitraryDemand;
	}

	/**
	 * Sets Talon to standard -1 to 1 voltage control
	 * 
	 * @param power
	 */
	public void setPercentOutput(double power) {
		controlMode = ControlMode.PercentOutput;
		setpoint = power;
	}

	/**
	 * Sets Talon to ControlMode.Current
	 * 
	 * @param current
	 *            in amps
	 */
	public void setCurrent(double current) {
		controlMode = ControlMode.Current;
		setpoint = current;
	}

	/**
	 * Uses the CANTalon 1D motion profile generator
	 * 
	 * @param setpoint
	 *            target position in native units
	 * @param accel
	 *            max acceleration and deceleration
	 * @param cruiseVelocity
	 *            cruise velocity to max out at
	 */
	public void setMotionMagic(double setpoint, Gains gains, int cruiseVelocity, int accel) {
		controlMode = ControlMode.MotionMagic;
		this.setpoint = setpoint;
		this.gains = gains;
		this.accel = accel;
		this.cruiseVel = cruiseVelocity;
		this.gains = gains;
	}

	/**
	 * Sets Talon to ControlMode.Disabled
	 */
	public void setDisabled() {
		this.controlMode = ControlMode.Disabled;
	}

	public String toString() {
		String name = "";
		if(controlMode == null) {
			name += "null";
		} else {
			name += controlMode.toString();
		}
		name += " " + getSetpoint();
		return name;
	}

	/**
	 * Used for unit tests to compare drive signal values
	 */
	@Override
	public boolean equals(Object other) {
		return ((TalonSRXOutput) other).getSetpoint() == this.getSetpoint() && ((TalonSRXOutput) other).controlMode == this.controlMode
				&& ((TalonSRXOutput) other).gains.equals(this.gains);
	}
}