package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.CircularBuffer;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.Gains;

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

		double multiplier = 1;
		if (state.indexerHasTopBall) {
			multiplier = 0.0;
		}

		switch (commands.indexerWantedBeltState) {
			case INDEX:
				mBlocked = false;
				mIndexerVelocityOutputs.add(state.indexerMasterVelocity);
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					if (mIndexerVelocityOutputs.numberOfOccurrences(d -> (d < mConfig.sparkIndexingOutput * kStuckPercent) && d > kForwardThreshold) > 20) {
						setSparkMaxVelocity(-mConfig.reversingOutput);
					} else {
						setSparkMaxProfiledVelocity(mConfig.sparkIndexingOutput);
					}
				} else {
					setSparkMaxProfiledVelocity(mConfig.sparkIndexingOutput);
				}
				setVTalonOutput(mConfig.leftTalonIndexingOutput, mConfig.rightTalonIndexingOutput, multiplier);
				break;
			case IDLE:
				mSlaveSparkOutput.setIdle();
				mMasterSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlocked = true;
				break;
			case MANUAL:
				setSparkMaxProfiledVelocity(commands.indexerManualVelocity);
				setVTalonOutput(Math.signum(commands.indexerManualVelocity) * mConfig.leftTalonIndexingOutput,
						Math.signum(commands.indexerManualVelocity) * mConfig.rightTalonIndexingOutput, multiplier);
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
						setSparkMaxProfiledVelocity(mConfig.feedingOutput);
					}
				} else {
					setSparkMaxProfiledVelocity(mConfig.feedingOutput);
				}
				mBlocked = false;
				setVTalonOutput(mConfig.leftTalonIndexingOutput, mConfig.rightTalonIndexingOutput, multiplier);
				break;
			case WAITING_TO_FEED:
				mSlaveSparkOutput.setIdle();
				mMasterSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlocked = false;
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

	public void setVTalonOutput(double leftTalonOutput, double rightTalonOutput, double multiplier) {
		mLeftVTalonOutput.setPercentOutput(leftTalonOutput * multiplier);
		mRightVTalonOutput.setPercentOutput(rightTalonOutput * multiplier);
	}

	public void setSparkMaxVelocity(double velocity) {
		var masterGains = new Gains(mConfig.masterVelocityGains.p, mConfig.masterVelocityGains.i, mConfig.masterVelocityGains.d, mConfig.masterVelocityGains.f, mConfig.masterVelocityGains.iZone, mConfig.masterVelocityGains.iMax);
		var slaveGains = new Gains(mConfig.slaveVelocityGains.p, mConfig.slaveVelocityGains.i, mConfig.slaveVelocityGains.d, mConfig.slaveVelocityGains.f, mConfig.slaveVelocityGains.iZone, mConfig.slaveVelocityGains.iMax);
		mMasterSparkOutput.setTargetVelocity(velocity, masterGains);
		mSlaveSparkOutput.setTargetVelocity(velocity, slaveGains);
	}

	public void setSparkMaxProfiledVelocity(double velocity) {
		mMasterSparkOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
		mSlaveSparkOutput.setTargetVelocityProfiled(velocity, mConfig.slaveVelocityGains);
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
