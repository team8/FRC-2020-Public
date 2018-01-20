package com.palyrobotics.frc2018.subsystems;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;

public class ElevatorSimulation {
	  // Stall Torque in N m
	  static final double kStallTorque = 0.71;
	  // Stall Current in Amps
	  static final double kStallCurrent = 134;
	  // Free Speed in RPM
	  static final double kFreeSpeed = 18730;
	  // Free Current in Amps
	  static final double kFreeCurrent = 0.7;
	  // Mass of the Elevator
	  static final double kMass = 30.0;

	  // Number of motors
	  static final double kNumMotors = 2.0;
	  // Resistance of the motor
	  static final double kResistance = 12.0 / kStallCurrent;
	  // Motor velocity constant
	  static final double Kv = ((kFreeSpeed / 60.0 * 2.0 * Math.PI) /
	             (12.0 - kResistance * kFreeCurrent));
	  // Torque constant
	  static final double Kt = (kNumMotors * kStallTorque) / kStallCurrent;
	  // Gear ratio
	  static final double kG = 42;
	  // Radius of pulley
	  static final double kr = 1.5;

	  // Control loop time step
	  static final double kDt = 0.010;

	  // Max elevator height in inches
	  static final double kElevatorHeight = 85;

	  // V = I * R + omega / Kv
	  // torque = Kt * I

	  double GetAcceleration(double voltage) {
	    return -Kt * kG * kG / (Kv * kResistance * kr * kr * kMass) * velocity_ +
	           kG * Kt / (kResistance * kr * kMass) * voltage;
	  }

	  double position_ = 0.1;
	  double velocity_ = 0;
	  double voltage_ = 0;
	  double offset_ = -0.1;
	  Elevator mElevator = Elevator.getInstance();

	  double encoder() {
		  return position_ + offset_;
	  }

	  boolean bottomHalleffect() {
		  return position_ < 0.0 && position_ > -0.01;
	  }

	  boolean topHallEffect() {
		  return position_ < kElevatorHeight+0.005 && position_ > kElevatorHeight-0.005;
	  }

	  double current_time = 0;

	  void simulateTime(double time, Commands commands, double voltage) {
	RobotState robotState = new RobotState();
	    while (time > 0) {
		robotState.elevatorBottomHFX = this.bottomHalleffect();
		robotState.elevatorTopHFX = this.topHallEffect();
		robotState.elevatorPosition = position_;
		mElevator.update(commands, robotState);
		final double current_dt = Math.min(time, 0.001);
		position_ += current_dt * velocity_;
		velocity_ += current_dt * GetAcceleration(voltage);
		time -= 0.001;
	    }
	  }
}
