package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerFeedSingleRoutine extends TimeoutRoutineBase {

	private boolean mHasBallPassed;

	public IndexerFeedSingleRoutine() {
		super(3.0);
	}

	public IndexerFeedSingleRoutine(double timeoutSeconds) {
		super(timeoutSeconds);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.FEED_SINGLE;
		if (!mHasBallPassed && state.indexerHasTopBall) {
			mHasBallPassed = true;
		}
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return mHasBallPassed;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
