package com.palyrobotics.frc2020.behavior;

import java.util.Set;
import java.util.function.Predicate;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class ConditionalRoutine extends RoutineBase {

	protected RoutineBase mRunningRoutine;
	protected RoutineBase mDefaultRoutine;
	protected RoutineBase mAlternateRoutine;
	protected Predicate<RobotState> mPredicate;

	private boolean mRunningDefault = false;

	public ConditionalRoutine(RoutineBase routine, Predicate<RobotState> predicate) {
		this(routine, null, predicate);
	}

	public ConditionalRoutine(RoutineBase defaultRoutine, RoutineBase alternateRoutine, Predicate<RobotState> predicate) {
		mDefaultRoutine = defaultRoutine;
		mRunningRoutine = defaultRoutine;
		mAlternateRoutine = alternateRoutine;
		mPredicate = predicate;
	}

	private void checkState(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (!mPredicate.test(state) && !mRunningDefault) {
			mRunningRoutine.stop(commands, state);
			mRunningRoutine = mAlternateRoutine;
			mRunningDefault = true;
		}
	}

	@Override
	protected void start(Commands commands, RobotState state) {
		checkState(commands, state);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		checkState(commands, state);
		if (mRunningRoutine != null) {
			mRunningRoutine.execute(commands, state);
		}
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		if (mRunningRoutine != null) {
			mRunningRoutine.stop(commands, state);
		}
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return !mPredicate.test(state) || mRunningRoutine == null || mRunningRoutine.isFinished();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return mRunningRoutine.getRequiredSubsystems();
	}
}
