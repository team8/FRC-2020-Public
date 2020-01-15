package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public abstract class Subsystem {

	private final String mName;

	protected Subsystem() {
		String className = getClass().getSimpleName();
		mName = Character.toLowerCase(className.charAt(0)) + className.substring(1); // Make first character lowercase
																						// to match JSON conventions
	}

	public abstract void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState);

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getStatus() {
		return null;
	}
}