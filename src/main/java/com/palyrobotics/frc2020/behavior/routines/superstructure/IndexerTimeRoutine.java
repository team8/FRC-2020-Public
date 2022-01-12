package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.Indexer.BeltState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerTimeRoutine extends TimeoutRoutineBase {

	boolean goBackwards = false;

	public IndexerTimeRoutine(double durationSeconds) {
		super(durationSeconds);
	}

	public IndexerTimeRoutine(double durationSeconds, boolean backwards) {
		super(durationSeconds);
		goBackwards = backwards;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		if (!goBackwards) {
			commands.indexerWantedBeltState = BeltState.INDEX;
			commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
		} else {
			//commands.indexerManualVelocity = 0.2 * Configs.get(IndexerConfig.class).feedingOutput;
			//commands.indexerWantedBeltState = BeltState.MANUAL;
			commands.indexerWantedBeltState = BeltState.REVERSING;
			commands.indexerWantedHopperState = Indexer.HopperState.CLOSED;
		}

	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = BeltState.IDLE;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
