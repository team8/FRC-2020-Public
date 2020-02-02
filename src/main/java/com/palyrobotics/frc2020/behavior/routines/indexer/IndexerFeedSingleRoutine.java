package com.palyrobotics.frc2020.behavior.routines.indexer;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerFeedSingleRoutine extends TimeoutRoutineBase {

	public IndexerFeedSingleRoutine() {
		super(3.0);
	}

	public IndexerFeedSingleRoutine(double timeoutSeconds) {
		super(timeoutSeconds);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState robotState) {
		commands.indexerWantedBeltState = Indexer.BeltState.FEED_SINGLE;
	}

	@Override
	protected void stop(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return state.indexerHasTopBall;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}