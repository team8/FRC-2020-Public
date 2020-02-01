package com.palyrobotics.frc2020.behavior.routines.indexer;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerTimeRoutine extends TimedRoutine {

	public IndexerTimeRoutine(double duration) {
		super(duration);
	}

	public IndexerTimeRoutine() {
		super(1);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedState = Indexer.State.INDEX;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
