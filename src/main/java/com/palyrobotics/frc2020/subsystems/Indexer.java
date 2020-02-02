package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Indexer extends SubsystemBase {

	public enum BeltState {
		IDLE, INDEX, WAITING_TO_FEED, FEED_SINGLE, FEED_ALL
	}

	public enum HopperState {
		OPEN, CLOSED
	}

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean mHopperOutput = true;
	private boolean mBlockOutput = false;

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		switch (commands.indexerWantedBeltState) {
			case IDLE:
				mOutput.setIdle();
				mBlockOutput = false;
				break;
			case INDEX:
				mOutput.setPercentOutput(mConfig.indexingOutput);
				mBlockOutput = false;
				break;
			case WAITING_TO_FEED:
				mOutput.setIdle();
				mBlockOutput = true;
				break;
			case FEED_SINGLE:
				mOutput.setPercentOutput(mConfig.feedingOutput);
				mBlockOutput = true;
				break;
			case FEED_ALL:
				mOutput.setPercentOutput(mConfig.indexingOutput);
				mBlockOutput = true;
				break;
		}
		mHopperOutput = commands.indexerWantedUpDownState == HopperState.CLOSED;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getHopperOutput() {
		return mHopperOutput;
	}

	public boolean getBlockOutput() {
		return mBlockOutput;
	}
}
