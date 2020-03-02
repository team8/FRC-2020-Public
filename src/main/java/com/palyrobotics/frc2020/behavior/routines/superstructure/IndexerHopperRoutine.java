package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.OneUpdateRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerHopperRoutine extends OneUpdateRoutineBase {

	private Indexer.HopperState mWantedHopperState;

	public IndexerHopperRoutine(Indexer.HopperState wantedHopperState) {
		mWantedHopperState = wantedHopperState;
	}

	@Override
	protected void updateOnce(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedHopperState = mWantedHopperState;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
