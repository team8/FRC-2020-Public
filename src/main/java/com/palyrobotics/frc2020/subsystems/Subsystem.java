package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.StringUtil;

public abstract class Subsystem {

	private final String mName;

	protected Subsystem() {
		mName = StringUtil.classToJsonName(getClass());
	}

	public abstract void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState);

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return mName;
	}
}
