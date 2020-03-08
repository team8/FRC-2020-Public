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

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mMasterSparkOutput = new ControllerOutput(),
			mSlaveSparkOutput = new ControllerOutput(),
			mLeftVTalonOutput = new ControllerOutput(),
			mRightVTalonOutput = new ControllerOutput();
	private CircularBuffer<Double> mMasterVelocityFilter = new CircularBuffer<>(30);
	private boolean mHopperOutput, mBlockOutput;

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double multiplier = 1.0;
		if (state.indexerHasBackBall) {
			multiplier = 0.0;
		}
//		if ((Math.round(Timer.getFPGATimestamp() * mConfig.pulsePeriod) % 2L) == 0L) {
//			leftMultiplier = 0.0;
//			rightMultiplier = 0.6;
//		} else {
//			leftMultiplier = 0.4;
//			rightMultiplier = 0.0;
//		}

		switch (commands.indexerWantedBeltState) {
			case IDLE:
				mMasterSparkOutput.setIdle();
				mSlaveSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlockOutput = true;
				break;
			case INDEX:
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					mMasterVelocityFilter.add(state.indexerMasterVelocity);
					if (mMasterVelocityFilter.numberOfOccurrences(d -> (d < mConfig.sparkIndexingOutput * 0.2) && d > -0.1) > 20) {
						setVelocity(-mConfig.reversingOutput);
					} else {
						setProfiledVelocity(mConfig.sparkIndexingOutput);
					}
				} else {
					setProfiledVelocity(mConfig.sparkIndexingOutput);
				}
				setVTalonOutput(mConfig.leftTalonIndexingOutput, mConfig.rightTalonIndexingOutput, multiplier);
				mBlockOutput = true;
				break;
			case WAITING_TO_FEED:
				mMasterSparkOutput.setIdle();
				mSlaveSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlockOutput = false;
				break;
			case FEED_SINGLE:
			case FEED_ALL:
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					mMasterVelocityFilter.add(state.indexerMasterVelocity);
					if (mMasterVelocityFilter.numberOfOccurrences(d -> (d < mConfig.feedingOutput * 0.2) && d > -0.1) > 20) {
						setVelocity(-mConfig.reversingOutput);
					} else {
						setProfiledVelocity(mConfig.feedingOutput);
					}
				} else {
					setProfiledVelocity(mConfig.feedingOutput);
				}
				setVTalonOutput(mConfig.leftTalonIndexingOutput, mConfig.rightTalonIndexingOutput, multiplier);
				mBlockOutput = false;
				break;
			case REVERSING:
				setVelocity(-mConfig.reversingOutput);
				setVTalonOutput(-mConfig.leftTalonIndexingOutput, -mConfig.rightTalonIndexingOutput, multiplier);
				mBlockOutput = false;
				break;
			case MANUAL:
				setProfiledVelocity(commands.indexerManualVelocity);
				setVTalonOutput(
						Math.signum(commands.indexerManualVelocity) * mConfig.leftTalonIndexingOutput,
						Math.signum(commands.indexerManualVelocity) * mConfig.rightTalonIndexingOutput, multiplier);
				break;
		}
		if (commands.indexerWantedBeltState != BeltState.INDEX && commands.indexerWantedBeltState != BeltState.FEED_ALL) mMasterVelocityFilter.clear();
		mHopperOutput = commands.indexerWantedHopperState == HopperState.OPEN;
	}

	private void setVTalonOutput(double leftOutput, double rightOutput, double multiplier) {
		mLeftVTalonOutput.setPercentOutput(leftOutput * multiplier);
		mRightVTalonOutput.setPercentOutput(rightOutput * multiplier);
	}

	private void setProfiledVelocity(double velocity) {
		mMasterSparkOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
		mSlaveSparkOutput.setTargetVelocityProfiled(velocity, mConfig.slaveVelocityGains);
	}

	private void setVelocity(double velocity) {
		var masterGains = new Gains(mConfig.masterVelocityGains.p, mConfig.masterVelocityGains.i, mConfig.masterVelocityGains.d, mConfig.masterVelocityGains.f, mConfig.masterVelocityGains.iZone, mConfig.masterVelocityGains.iMax);
		var slaveGains = new Gains(mConfig.slaveVelocityGains.p, mConfig.slaveVelocityGains.i, mConfig.slaveVelocityGains.d, mConfig.slaveVelocityGains.f, mConfig.slaveVelocityGains.iZone, mConfig.slaveVelocityGains.iMax);
		mMasterSparkOutput.setTargetVelocity(velocity, masterGains);
		mSlaveSparkOutput.setTargetVelocity(velocity, slaveGains);
	}

	public ControllerOutput getMasterSparkOutput() {
		return mMasterSparkOutput;
	}

	public ControllerOutput getSlaveSparkOutput() {
		return mSlaveSparkOutput;
	}

	public ControllerOutput getLeftVTalonOutput() {
		return mLeftVTalonOutput;
	}

	public ControllerOutput getRightVTalonOutput() {
		return mRightVTalonOutput;
	}

	public boolean getHopperOutput() {
		return mHopperOutput;
	}

	public boolean getBlockOutput() {
		return mBlockOutput;
	}

	public enum BeltState {
		IDLE, INDEX, WAITING_TO_FEED, FEED_SINGLE, FEED_ALL, REVERSING, MANUAL
	}

	public enum HopperState {
		OPEN, CLOSED
	}
}
