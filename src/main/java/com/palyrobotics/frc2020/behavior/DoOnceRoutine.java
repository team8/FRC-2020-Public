package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.robot.Commands;

public abstract class DoOnceRoutine extends Routine {

	@Override
	protected final void update(Commands commands) {
		doOnce(commands);
	}

	protected abstract void doOnce(Commands commands);

	@Override
	public final boolean checkFinished() {
		return true;
	}
}