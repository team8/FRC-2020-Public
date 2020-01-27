package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

/**
 * Completes after one update cycle, not immediately. It is possible that
 * {@link Commands} can indend to modify {@link RobotState}. However, we
 */
public abstract class OneUpdateRoutine extends RoutineBase {

	private int mCounter;

	@Override
	protected final void update(Commands commands, RobotState state) {
		mCounter++;
		updateOnce(commands);
	}

	protected abstract void updateOnce(Commands commands);

	@Override
	public final boolean checkFinished(@ReadOnly RobotState state) {
		return mCounter > 1;
	}
}
