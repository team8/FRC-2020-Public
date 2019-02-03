package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;

public abstract class Subsystem {
	private String mName;

	public CSVWriter mWriter;

	public Subsystem(String name) {
		this.mName = name;
		this.mWriter = CSVWriter.getInstance();
	}

	//Updates the subsystem with current commands and state
	public abstract void update(Commands commands, RobotState robotState);

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return mName;
	}

	public void start() {
	}

	public void stop() {
	}

	public String getStatus() {
		return null;
	}
}