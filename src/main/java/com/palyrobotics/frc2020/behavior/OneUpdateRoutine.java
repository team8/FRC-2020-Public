package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public abstract class OneUpdateRoutine extends RoutineBase {

	@Override
	protected final void update(Commands commands, RobotState state) {
		updateOnce(commands);
	}

	protected abstract void updateOnce(Commands commands);

	@Override
	public final boolean checkFinished(@ReadOnly RobotState state) {
		return true;
	}
}
