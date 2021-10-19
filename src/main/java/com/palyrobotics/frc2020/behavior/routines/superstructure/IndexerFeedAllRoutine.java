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
//	private boolean mDoReverse;

	public IndexerFeedAllRoutine(double timeoutSeconds, boolean waitForFlywheel, boolean doReverse) {
		super(timeoutSeconds);
		mWaitForFlywheel = waitForFlywheel;
//		mDoReverse = doReverse;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
//		boolean shouldReverse = mTimer.get() < mConfig.rever;
		Indexer.ColumnState columnState = !mWaitForFlywheel || state.shooterIsReadyToShoot ?
				Indexer.ColumnState.FEED :
				Indexer.ColumnState.IDLE;
//		handle(commands, /* shouldReverse */, columnState);
//		commands.indexerWantedHopperState = Indexer.HopperState.OPEN;
	}

//
//	private void handle(Commands commands, boolean shouldReverse, Indexer.ColumnState columnState) {
//		if (mDoReverse) {
//			commands.indexerColumnWantedState = shouldReverse ? Indexer.ColumnState.REVERSE_FEED : columnState;
//			if (shouldReverse) {
//				commands.setIntakeRunning(0);
//			} else {
//				commands.setIntakeStowed();
//			}
//		} else {
//			commands.indexerColumnWantedState = columnState;
//		}
//	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerColumnWantedState = Indexer.ColumnState.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer, mIntake);
	}
}
