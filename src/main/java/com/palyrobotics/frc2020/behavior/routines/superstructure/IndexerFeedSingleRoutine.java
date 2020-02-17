package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;

public class IndexerFeedSingleRoutine extends TimeoutRoutineBase {

	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private boolean mLastIndexerHasTopBall;

	public IndexerFeedSingleRoutine() {
		super(3.0);
	}

	public IndexerFeedSingleRoutine(double timeoutSeconds) {
		super(timeoutSeconds);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
		commands.indexerWantedBeltState = mTimer.get() < mConfig.reverseTime ? Indexer.BeltState.REVERSING : Indexer.BeltState.FEED_SINGLE;
//		if (state.shooterIsReadyToShoot) {
//			commands.indexerWantedBeltState = Indexer.BeltState.FEED_SINGLE;
//		} else {
//			commands.indexerWantedBeltState = Indexer.BeltState.WAITING_TO_FEED;
//		}
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.WAITING_TO_FEED;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		boolean isFinished = mLastIndexerHasTopBall && !state.indexerHasTopBall;
		mLastIndexerHasTopBall = state.indexerHasTopBall;
		return isFinished;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
