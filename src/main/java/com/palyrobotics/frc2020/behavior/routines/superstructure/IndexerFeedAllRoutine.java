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

public class IndexerFeedAllRoutine extends TimeoutRoutineBase {

	private final IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private final boolean mWaitForFlywheel;
	private boolean mDoReverse;

	public IndexerFeedAllRoutine() {
		this(5.0);
	}

	public IndexerFeedAllRoutine(double timeoutSeconds) {
		this(timeoutSeconds, false, true);
	}

	public IndexerFeedAllRoutine(double timeoutSeconds, boolean waitForFlywheel, boolean doReverse) {
		super(timeoutSeconds);
		mWaitForFlywheel = waitForFlywheel;
		mDoReverse = doReverse;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		boolean shouldReverse = mTimer.get() < mConfig.reverseTime;
		if (!mWaitForFlywheel || state.shooterIsReadyToShoot) {
			if (mDoReverse) {
				commands.indexerWantedBeltState = shouldReverse ? Indexer.BeltState.REVERSING : Indexer.BeltState.FEED_ALL;
			} else {
				commands.indexerWantedBeltState = Indexer.BeltState.FEED_ALL;
			}
		} else {
			if (mDoReverse) {
				commands.indexerWantedBeltState = shouldReverse ? Indexer.BeltState.REVERSING : Indexer.BeltState.WAITING_TO_FEED;
			} else {
				commands.indexerWantedBeltState = Indexer.BeltState.WAITING_TO_FEED;
			}
		}
		commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
