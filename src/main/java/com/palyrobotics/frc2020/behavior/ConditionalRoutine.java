package com.palyrobotics.frc2020.behavior;

import java.util.Set;
import java.util.function.Predicate;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class ConditionalRoutine extends RoutineBase {

	protected RoutineBase mRoutine;
	protected Predicate<RobotState> mPredicate;

	public ConditionalRoutine(RoutineBase routine, Predicate<RobotState> predicate) {
		mRoutine = routine;
		mPredicate = predicate;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		mRoutine.execute(commands, state);
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		mRoutine.stop(commands, state);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return !mPredicate.test(state) || mRoutine.isFinished();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return mRoutine.getRequiredSubsystems();
	}
}
