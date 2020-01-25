package com.palyrobotics.frc2020.behavior;

import java.util.Set;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.*;

public abstract class RoutineBase {

	private enum RoutineState {
		INIT, RUNNING, FINISHED
	}

	protected final Climber mClimber = Climber.getInstance();
	protected final Drive mDrive = Drive.getInstance();
	protected final Indexer mIndexer = Indexer.getInstance();
	protected final Intake mIntake = Intake.getInstance();
	protected final Spinner mSpinner = Spinner.getInstance();
	private RoutineState mState = RoutineState.INIT;

	public final boolean execute(Commands commands, @ReadOnly RobotState state) {
		if (mState == RoutineState.INIT) {
			start(state);
			mState = RoutineState.RUNNING;
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

	protected void start(@ReadOnly RobotState state) {
	}

	@Override
	public String toString() {
		return getName();
	}

	protected void update(Commands commands, @ReadOnly RobotState state) {
	}

	public boolean checkFinished(@ReadOnly RobotState state) {
		return true;
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public final boolean isFinished() {
		return mState == RoutineState.FINISHED;
	}

	// Store subsystems which are required by this routine, preventing routines from
	// overlapping
	public abstract Set<SubsystemBase> getRequiredSubsystems();
}
