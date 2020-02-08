package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class VisionSpinupAndShootRoutine extends TimeoutRoutineBase {

	boolean prevShooterReadyToShoot = false;
	boolean becameReadyToShoot = false;

	public VisionSpinupAndShootRoutine(double durationSeconds) {
		super(durationSeconds);
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.indexerWantedBeltState = Indexer.BeltState.WAITING_TO_FEED;
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		commands.setShooterVisionAssisted();
		if (!becameReadyToShoot && !prevShooterReadyToShoot && state.shooterIsReadyToShoot) {
			becameReadyToShoot = true;
			commands.addWantedRoutine(new IndexerFeedAllRoutine());
		}
		prevShooterReadyToShoot = state.shooterIsReadyToShoot;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.setShooterIdle();
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return state.shooterIsReadyToShoot;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mShooter, mIndexer);
	}
}
