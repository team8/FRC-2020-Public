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
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;

public class IndexerCaterpillar extends RoutineBase {

	private double initialEncoderPosition;
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return Math.abs(state.indexerEncoderPosition - initialEncoderPosition - mConfig.caterpillarTargetEncoderTicks) < mConfig.caterpillarTolerance;
	}

	@Override
	protected void start(Commands commands, @ReadOnly RobotState state) {
		initialEncoderPosition = state.indexerEncoderPosition;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = Indexer.BeltState.CATERPILLAR;
		LiveGraph.add("targetEncoder", initialEncoderPosition + mConfig.caterpillarTargetEncoderTicks);
		LiveGraph.add("currentEncoder", state.indexerEncoderPosition);

	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
