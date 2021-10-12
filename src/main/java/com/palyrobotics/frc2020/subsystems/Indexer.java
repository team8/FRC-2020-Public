package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.CircularBuffer;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Indexer extends SubsystemBase {

	private Indexer() {
	}

	public enum BeltState {
		INDEX, IDLE, MANUAL, REVERSING, FEED_SINGLE, FEED_ALL, WAITING_TO_FEED
	}

	public enum HopperState {
		CLOSED, OPEN
	}

	private boolean mHopperOutput;
	private boolean mBlocked;
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private CircularBuffer<Double> mIndexerVelocityOutputs = new CircularBuffer<>(30);
	private double kStuckPercent = 0.2;
	private double kForwardThreshold = -0.1;
	private ControllerOutput mSlaveSparkOutput = new ControllerOutput();
	private ControllerOutput mMasterSparkOutput = new ControllerOutput();
	private ControllerOutput mLeftVTalonOutput = new ControllerOutput();
	private ControllerOutput mRightVTalonOutput = new ControllerOutput();

	private static Indexer sIndexer = new Indexer();

	public static Indexer getInstance() {
		return sIndexer;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		switch (commands.indexerWantedBeltState) {
			case INDEX:
				mBlocked = false;
				mIndexerVelocityOutputs.add(state.indexerMasterVelocity);
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					if (mIndexerVelocityOutputs.numberOfOccurrences(d -> (d < mConfig.sparkIndexingOutput * kStuckPercent) && d > kForwardThreshold) > 20) {
						setTalonTargetVelocity(-mConfig.reversingOutput);
					} else {
						setTalonTargetProfiledVelocity(mConfig.sparkIndexingOutput);
					}
				} else {
					setTalonTargetProfiledVelocity(mConfig.sparkIndexingOutput);
				}
				break;
			case IDLE:
				mSlaveSparkOutput.setIdle();
				mMasterSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlocked = true;
				break;
			case MANUAL:
				mLeftVTalonOutput.setTargetVelocityProfiled(Math.signum(commands.indexerManualVelocity) * mConfig.leftTalonIndexingOutput, mConfig.masterVelocityGains);
				mRightVTalonOutput.setTargetVelocityProfiled(Math.signum(commands.indexerManualVelocity) * mConfig.rightTalonIndexingOutput, mConfig.masterVelocityGains);
				break;
			case REVERSING:
				break;
			case FEED_SINGLE:
			case FEED_ALL:
				mIndexerVelocityOutputs.add(state.indexerMasterVelocity);
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					if (mIndexerVelocityOutputs.numberOfOccurrences(d -> (d < mConfig.reversingOutput * kStuckPercent) && d > kForwardThreshold) > 20) {
						setSparkMaxVelocity(-mConfig.reversingOutput);
					} else {
						setSparkMaxProfiledVelocity(mConfig.sparkIndexingOutput);
					}
				} else {
					setSparkMaxProfiledVelocity(mConfig.sparkIndexingOutput);
				}
				mBlocked = false;
				break;
			case WAITING_TO_FEED:
				mSlaveSparkOutput.setIdle();
				mMasterSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				break;

		}

		if (commands.indexerWantedHopperState == HopperState.OPEN) {
			mHopperOutput = true;
		} else {
			mHopperOutput = false;
		}
		if (commands.indexerWantedBeltState == BeltState.IDLE) {
			mIndexerVelocityOutputs.clear();
		}
	}

	public void setTalonTargetProfiledVelocity(double velocity) {
		mLeftVTalonOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
		mRightVTalonOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
	}

	public void setTalonTargetVelocity(double velocity) {
		mLeftVTalonOutput.setTargetVelocity(velocity, mConfig.masterVelocityGains);
		mRightVTalonOutput.setTargetVelocity(velocity, mConfig.masterVelocityGains);
	}

	public void setSparkMaxVelocity(double velocity) {
		mSlaveSparkOutput.setTargetVelocity(velocity, mConfig.masterVelocityGains);
		mMasterSparkOutput.setTargetVelocity(velocity, mConfig.masterVelocityGains);
	}

	public void setSparkMaxProfiledVelocity(double velocity) {
		mSlaveSparkOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
		mMasterSparkOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
	}

	public boolean getHopperOutput() {
		return mHopperOutput;
	}

	public boolean getBlockOutput() {
		return mBlocked;
	}

	public ControllerOutput getSlaveSparkOutput() {
		return mSlaveSparkOutput;
	}

	public ControllerOutput getMasterSparkOutput() {
		return mMasterSparkOutput;
	}

	public ControllerOutput getLeftVTalonOutput() {
		return mLeftVTalonOutput;
	}

	public ControllerOutput getRightVTalonOutput() {
		return mRightVTalonOutput;
	}

}
