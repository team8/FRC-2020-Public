package com.palyrobotics.frc2020.behavior;

import java.util.Set;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Subsystem;

/**
 *
 */
public abstract class Routine {

	private enum RoutineState {
		INIT, RUNNING, FINISHED
	}

	protected final Drive mDrive = Drive.getInstance();

	private RoutineState mState = RoutineState.INIT;

	public final boolean execute(Commands commands) {
		if (mState == RoutineState.INIT) {
			start();
			mState = RoutineState.RUNNING;
		} else if (mState == RoutineState.FINISHED) {
			throw new IllegalStateException(
					String.format("Routine %s already finished! Should not be updated.", toString()));
		}
		update(commands);
		if (checkFinished()) {
			mState = RoutineState.FINISHED;
			return true;
		}
		return false;
	}

	protected void start() {

	}

	protected void update(Commands commands) {

	}

	public boolean checkFinished() {
		return true;
	}

	public final boolean isFinished() {
		return mState == RoutineState.FINISHED;
	}

	// Store subsystems which are required by this routine, preventing routines from
	// overlapping
	public abstract Set<Subsystem> getRequiredSubsystems();

	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getName();
	}
}
