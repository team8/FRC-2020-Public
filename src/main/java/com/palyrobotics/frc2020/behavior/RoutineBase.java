package com.palyrobotics.frc2020.behavior;

import java.util.Set;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;

public abstract class RoutineBase {

	private enum RoutineState {
		INIT, RUNNING, FINISHED
	}

	/** Only should be used for {@link #getRequiredSubsystems()} */
	protected final Climber mClimber = Climber.getInstance();
	protected final Drive mDrive = Drive.getInstance();
	protected final Indexer mIndexer = Indexer.getInstance();
	protected final Intake mIntake = Intake.getInstance();
	protected final Spinner mSpinner = Spinner.getInstance();
	private RoutineState mState = RoutineState.INIT;

	/**
	 * Handles changing the {@link #mState}.
	 *
	 * @param commands Routines can only modify desired {@link Commands}.
	 * @param state    Should only read {@link RobotState}.
	 * @return Is the routine finished.
	 * @see #start(Commands, RobotState)
	 * @see #update(Commands, RobotState)
	 */
	public final boolean execute(Commands commands, @ReadOnly RobotState state) {
		if (mState == RoutineState.INIT) {
			// Check if a routine is finished before even starting it.
			// This avoids calling any sort of update that would modify Commands when not
			// required.
			if (checkFinished(state)) {
				mState = RoutineState.FINISHED;
				return true;
			} else {
				start(commands, state);
				mState = RoutineState.RUNNING;
			}
		} else if (mState == RoutineState.FINISHED) {
			throw new IllegalStateException(
					String.format("Routine %s already finished! Should not be updated.", toString()));
		}
		update(commands, state);
		if (checkFinished(state)) {
			mState = RoutineState.FINISHED;
			return true;
		}
		return false;
	}

	/**
	 * Check with {@link RobotState} to see if we are done and should no longer be
	 * updated.
	 */
	public abstract boolean checkFinished(@ReadOnly RobotState state);

	/**
	 * Called on the first update cycle before
	 * {@link #update(Commands, RobotState)}, unless we are already finished.
	 *
	 * @see #checkFinished(RobotState)
	 */
	protected void start(Commands commands, @ReadOnly RobotState state) {
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Called every update until we are finished.
	 *
	 * @see #checkFinished(RobotState)
	 */
	protected void update(Commands commands, @ReadOnly RobotState state) {
	}

	public String getName() {
		return Util.classToJsonName(getClass());
	}

	/**
	 * @see #checkFinished(RobotState)
	 */
	public final boolean isFinished() {
		return mState == RoutineState.FINISHED;
	}

	/**
	 * Store subsystems which are required by this routine, preventing routines from
	 * overlapping
	 */
	public abstract Set<SubsystemBase> getRequiredSubsystems();
}
