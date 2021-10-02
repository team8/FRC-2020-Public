package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IndexerFeedRoutine extends RoutineBase {

	private boolean mFirstRun = true;

	@Override
	protected void update(Commands commands, RobotState state) {
		if (mFirstRun) {
			commands.indexerColumnWantedState = Indexer.ColumnState.UN_INDEX;
			commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.IDLE;
			mFirstRun = false;
			return;
		}
		commands.indexerColumnWantedState = Indexer.ColumnState.FEED;
		commands.indexerVSingulatorWantedState = Indexer.VSingulatorState.FORWARD;
	}

	@Override
	public boolean checkFinished(RobotState state) {
		return !state.joystickRightTriggerPressed;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIndexer);
	}
}
