package com.palyrobotics.frc2019.util;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.util.control.Gains;

import java.util.Objects;

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
    private double setPoint; //Encoder ticks
    private double arbitraryDemand; // Arbitrary demand used in the PIDF + PID[1]
    public int profile;
    public Gains gains;

    //Used for motion magic
    public int acceleration;
    public int cruiseVelocity;

    /**
     * Default constructor
     */
    public TalonSRXOutput() {
        controlMode = ControlMode.Disabled;
        setPoint = 0;
        profile = 0;
        gains = new Gains(0, 0, 0, 0, 0);

        acceleration = 0;
        cruiseVelocity = 0;
    }

    /**
     * Copy constructor
     *
     * @param talon output to copy
     */
    public TalonSRXOutput(TalonSRXOutput talon) {
        this.controlMode = talon.getControlMode();
        this.setPoint = talon.getSetPoint();
        this.profile = talon.profile;
        this.gains = talon.gains;

        this.acceleration = talon.acceleration;
        this.cruiseVelocity = talon.cruiseVelocity;
        this.profile = talon.profile;
    }

    public TalonSRXOutput(ControlMode controlMode, Gains gains, double setPoint) {
        this.controlMode = controlMode;
        this.setPoint = setPoint;
        profile = 0;
        this.gains = gains;

        acceleration = 0;
        cruiseVelocity = 0;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public double getSetPoint() {
        return setPoint;
    }

    public double getArbitraryFF() {
        return arbitraryDemand;
    }

    /**
     * Sets Talon to ControlMode.Velocity, velocity target control loop
     *
     * @param speed, target velocity (from -1023019 to 10230?)
     */
    public void setVelocity(double speed, Gains gains) {
        controlMode = ControlMode.Velocity;
        setPoint = speed;
        this.gains = gains;
    }

    /**
     * Sets Talon to ControlMode.Position
     *
     * @param setPoint, target distance in native units
     */
    public void setPosition(double setPoint, Gains gains) {
        controlMode = ControlMode.Position;
        this.setPoint = setPoint;
        this.gains = gains;
        this.arbitraryDemand = 0.0;
    }


    /**
     * Sets Talon to ControlMode.Position
     *
     * @param setPoint, target distance in native units
     */
    public void setPosition(double setPoint, Gains gains, double arbitraryDemand) {
        controlMode = ControlMode.Position;
        this.setPoint = setPoint;
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
        setPoint = power;
    }

    /**
     * Sets Talon to ControlMode.Current
     *
     * @param current in amps
     */
    public void setCurrent(double current) {
        controlMode = ControlMode.Current;
        setPoint = current;
    }

    /**
     * Uses the CANTalon 1D motion profile generator
     *
     * @param setPoint       target position in native units
     * @param acceleration   max acceleration and deceleration
     * @param cruiseVelocity cruise velocity to max out at
     */
    public void setMotionMagic(double setPoint, Gains gains, int cruiseVelocity, int acceleration) {
        controlMode = ControlMode.MotionMagic;
        this.setPoint = setPoint;
        this.gains = gains;
        this.acceleration = acceleration;
        this.cruiseVelocity = cruiseVelocity;
    }

    /**
     * Sets Talon to ControlMode.Disabled
     */
    public void setDisabled() {
        this.controlMode = ControlMode.Disabled;
    }

    public String toString() {
        String name = "";
        if (controlMode == null) {
            name += "null";
        } else {
            name += controlMode.toString();
        }
        name += " " + getSetPoint();
        return name;
    }

	@Override // Auto-generated
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		TalonSRXOutput otherTalon = (TalonSRXOutput) other;
		return Double.compare(otherTalon.setPoint, setPoint) == 0 &&
				Double.compare(otherTalon.arbitraryDemand, arbitraryDemand) == 0 &&
				profile == otherTalon.profile &&
				acceleration == otherTalon.acceleration &&
				cruiseVelocity == otherTalon.cruiseVelocity &&
				controlMode == otherTalon.controlMode &&
				gains.equals(otherTalon.gains);
	}

	@Override
	public int hashCode() {
		return Objects.hash(controlMode, setPoint, arbitraryDemand, profile, gains, acceleration, cruiseVelocity);
	}
}