package com.palyrobotics.frc2020.behavior.routines.superstructure;

import static com.palyrobotics.frc2020.config.constants.ShooterConstants.kTimeToShootPerBallSeconds;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

public class ShooterRoutine extends DriveAlignYawAssistedRoutine {

	private int mVisionPipeline;
	private double mFeedTimeSeconds;
	private Timer mReadyToShootTimer = new Timer();
	private boolean mHasStartedShooting;

	public ShooterRoutine(double yawDegrees, int ballCount, int visionPipeline) {
		super(yawDegrees, visionPipeline);
		mFeedTimeSeconds = ballCount * kTimeToShootPerBallSeconds;
		mVisionPipeline = visionPipeline;
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		super.update(commands, state);
		commands.setShooterVisionAssisted(mVisionPipeline);
		if (state.shooterIsReadyToShoot && super.checkIfFinishedEarly(state)) {
			mHasStartedShooting = true;
			mReadyToShootTimer.start();
		}
		commands.indexerWantedBeltState = mHasStartedShooting ? Indexer.BeltState.FEED_ALL : Indexer.BeltState.WAITING_TO_FEED;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		super.stop(commands, state);
		commands.setShooterIdle();
		commands.indexerWantedBeltState = Indexer.BeltState.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return mReadyToShootTimer.hasElapsed(mFeedTimeSeconds);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mShooter, mIndexer);
	}
}
