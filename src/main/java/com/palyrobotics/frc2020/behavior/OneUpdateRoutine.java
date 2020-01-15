package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.robot.Commands;

public abstract class OneUpdateRoutine extends Routine {

	@Override
	protected final void update(Commands commands) {
		updateOnce(commands);
	}

	protected abstract void updateOnce(Commands commands);

	@Override
	public final boolean checkFinished() {
		return true;
	}
}
