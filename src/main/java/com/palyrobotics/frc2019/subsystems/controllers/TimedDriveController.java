package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;

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
	public SparkSignal update(RobotState state) {

		SparkMaxOutput leftOutput = new SparkMaxOutput();
		SparkMaxOutput rightOutput = new SparkMaxOutput();

		leftOutput.setPercentOutput(voltage);
		rightOutput.setPercentOutput(voltage);
		return new SparkSignal(leftOutput, rightOutput);
	}

	@Override
	public Pose getSetpoint() {
		return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
	}

}
