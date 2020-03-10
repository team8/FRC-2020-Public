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

	public ConditionalRoutine(RoutineBase routine, Predicate<RobotState> predicate) {
		this(routine, null, predicate);
	}

	public ConditionalRoutine(RoutineBase routine, RoutineBase defaultCase, Predicate<RobotState> predicate) {
		mAlternateRoutine = routine;
		mDefaultRoutine = defaultCase;
		mPredicate = predicate;
	}

	@Override
	protected void start(Commands commands, RobotState state) {
		mRunningRoutine = mPredicate.test(state) ? mAlternateRoutine : mDefaultRoutine;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		mRunningRoutine.execute(commands, state);
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		mRunningRoutine.stop(commands, state);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return !mPredicate.test(state) || mRunningRoutine.isFinished();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return mRunningRoutine.getRequiredSubsystems();
	}
}
