package com.palyrobotics.frc2018.subsystems.controllers;

import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.subsystems.Drive.DriveController;
import com.palyrobotics.frc2018.util.DriveSignal;
import com.palyrobotics.frc2018.util.Pose;
import com.palyrobotics.frc2018.util.TalonSRXOutput;

public class TimedDriveController implements DriveController {

	private double voltage;
	private double time;
	private double startTime;

	public TimedDriveController(double voltage, double time) {
		this.voltage = voltage;
		this.time = time;
		this.startTime = System.currentTimeMillis();

	}

	@Override
	public boolean onTarget() {
		return System.currentTimeMillis() > startTime + time * 1000;
	}

	@Override
	public DriveSignal update(RobotState state) {

		TalonSRXOutput leftOutput = new TalonSRXOutput();
		TalonSRXOutput rightOutput = new TalonSRXOutput();

		leftOutput.setPercentOutput(voltage);
		rightOutput.setPercentOutput(voltage);
		return new DriveSignal(leftOutput, rightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
	}

}
