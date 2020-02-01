package com.palyrobotics.frc2020.behavior.routines.indexer;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerFeedRoutine extends TimeoutRoutineBase {

	public IndexerFeedRoutine() {
		super(3.0);
	}

	public IndexerFeedRoutine(double timeout) {
		super(timeout);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState robotState) {
		commands.indexerWantedState = Indexer.IndexerState.FEED;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return !state.hasTopBall;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
