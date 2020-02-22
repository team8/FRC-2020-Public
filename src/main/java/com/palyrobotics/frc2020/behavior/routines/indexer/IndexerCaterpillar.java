package com.palyrobotics.frc2020.behavior.routines.indexer;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;

public class IndexerCaterpillar extends RoutineBase {

	private double initialEncoderPosition;
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return (state.indexerEncoderPosition - initialEncoderPosition) > mConfig.caterpillarTargetEncoderTicks;
	}

	@Override
	protected void start(Commands commands, @ReadOnly RobotState state) {
		initialEncoderPosition = state.indexerEncoderPosition;
		mIndexer.setInitialEncoderPosition(initialEncoderPosition);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		if ((state.indexerEncoderPosition - initialEncoderPosition) < mConfig.caterpillarTargetEncoderTicks) {
			commands.indexerWantedBeltState = Indexer.BeltState.CATERPILLAR;
		} else {
			commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
		}
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
