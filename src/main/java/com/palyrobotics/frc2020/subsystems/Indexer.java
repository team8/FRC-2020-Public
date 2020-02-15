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
	private ControllerOutput mSparkOutput = new ControllerOutput(),
			mTalonOutput = new ControllerOutput();
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
				mSparkOutput.setIdle();
				mTalonOutput.setIdle();
				mBlockOutput = true;
				break;
			case INDEX:
				mSparkOutput.setTargetVelocity(mConfig.sparkIndexingOutput, mConfig.velocityGains);
				mTalonOutput.setPercentOutput(mConfig.talonIndexingOutput);
				mBlockOutput = true;
				break;
			case WAITING_TO_FEED:
				mSparkOutput.setIdle();
				mTalonOutput.setIdle();
				mBlockOutput = false;
				break;
			case FEED_SINGLE:
				mSparkOutput.setTargetVelocity(mConfig.feedingOutput, mConfig.velocityGains);
				mTalonOutput.setPercentOutput(mConfig.feedingOutput);
				mBlockOutput = false;
				break;
			case FEED_ALL:
				mSparkOutput.setTargetVelocity(mConfig.sparkIndexingOutput, mConfig.velocityGains);
				mTalonOutput.setPercentOutput(mConfig.talonIndexingOutput);
				mBlockOutput = false;
				break;
			case REVERSING:
				mSparkOutput.setTargetVelocity(-mConfig.sparkIndexingOutput, mConfig.velocityGains);
				mTalonOutput.setPercentOutput(-mConfig.talonIndexingOutput);
				mBlockOutput = false;
		}
		mHopperOutput = commands.indexerWantedHopperState == HopperState.OPEN;
	}

	public ControllerOutput getSparkOutput() {
		return mSparkOutput;
	}

	public ControllerOutput getTalonOutput() {
		return mTalonOutput;
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
