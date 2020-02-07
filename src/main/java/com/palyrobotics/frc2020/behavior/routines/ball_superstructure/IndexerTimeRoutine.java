package com.palyrobotics.frc2020.behavior.routines.ball_superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer.BeltState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerTimeRoutine extends TimeoutRoutineBase {

	public IndexerTimeRoutine(double durationSeconds) {
		super(durationSeconds);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.indexerWantedBeltState = BeltState.INDEX;
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
