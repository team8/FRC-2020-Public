package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Indexer extends SubsystemBase {

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mMasterSparkOutput = new ControllerOutput(),
			mSlaveSparkOutput = new ControllerOutput(),
			mLeftVTalonOutput = new ControllerOutput(),
			mRightVTalonOutput = new ControllerOutput();
	private boolean mHopperOutput, mBlockOutput;

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
//		double multiplier, breh;
//		if (((long) (Timer.getFPGATimestamp() * 4.0) % 2L) == 0L) {
//			multiplier = 0.0;
//			breh = 1.0;
//		} else {
//			multiplier = 1.0;
//			breh = 1.0; // 0.0, indexer
//		}

//		if (Math.sin(Timer.getFPGATimestamp() * 2 * Math.PI) > 0) {
//			multiplier = 0.0;
//			breh = 1.0;
//		} else {
//			multiplier = 1.0;
//			breh = 0.0;
//		}

//		multiplier = Math.sin(2 * Math.PI * Timer.getFPGATimestamp());
//		breh = Math.sin(2 * Math.PI * Timer.getFPGATimestamp() + Math.PI);
//		multiplier = 1.0;
//		breh = 1.0;
//		LiveGraph.add("multiplier", multiplier);

		switch (commands.indexerWantedBeltState) {
			case IDLE:
				mMasterSparkOutput.setIdle();
				mSlaveSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlockOutput = true;
				break;
			case INDEX:
				setIndexerVelocity(mConfig.sparkIndexingOutput);
				mLeftVTalonOutput.setPercentOutput(mConfig.leftTalonIndexingOutput);
				mRightVTalonOutput.setPercentOutput(mConfig.rightTalonIndexingOutput);
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
				setIndexerVelocity(mConfig.feedingOutput);
				mLeftVTalonOutput.setPercentOutput(mConfig.leftTalonIndexingOutput);
				mRightVTalonOutput.setPercentOutput(mConfig.rightTalonIndexingOutput);
				mBlockOutput = false;
				break;
			case FEED_ALL:
				setIndexerVelocity(mConfig.feedingOutput);
				mLeftVTalonOutput.setPercentOutput(mConfig.leftTalonIndexingOutput);
				mRightVTalonOutput.setPercentOutput(mConfig.rightTalonIndexingOutput);
				mBlockOutput = false;
				break;
			case REVERSING:
				setIndexerVelocity(-mConfig.reversingOutput);
				mLeftVTalonOutput.setPercentOutput(-mConfig.leftTalonIndexingOutput);
				mRightVTalonOutput.setPercentOutput(-mConfig.rightTalonIndexingOutput);
				mBlockOutput = false;
		}
		mHopperOutput = commands.indexerWantedHopperState == HopperState.OPEN;
	}

	private void setIndexerVelocity(double velocity) {
		mMasterSparkOutput.setTargetVelocity(velocity, mConfig.masterVelocityGains);
		mSlaveSparkOutput.setTargetVelocity(velocity, mConfig.slaveVelocityGains);
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
		IDLE, INDEX, WAITING_TO_FEED, FEED_SINGLE, FEED_ALL, REVERSING
	}

	public enum HopperState {
		OPEN, CLOSED
	}
}
