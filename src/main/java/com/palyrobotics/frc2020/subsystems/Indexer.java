package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;

import edu.wpi.first.wpilibj.Timer;

public class Indexer extends SubsystemBase {

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mOutput = new ControllerOutput(),
			mBottomOutput = new ControllerOutput();
	private boolean mHopperOutput, mBlockOutput;

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double multiplier, breh;
		if (((long) (Timer.getFPGATimestamp() * 4.0) % 2L) == 0L) {
			multiplier = 0.0;
			breh = 1.0;
		} else {
			multiplier = 1.0;
			breh = 0.0;
		}
//		if (Math.sin(Timer.getFPGATimestamp() * 1) > 0) {
//			multiplier = 0.0;
//		}
		LiveGraph.add("multiplier", multiplier);
		switch (commands.indexerWantedBeltState) {
			case IDLE:
				mOutput.setIdle();
				mBottomOutput.setIdle();
				mBlockOutput = true;
				break;
			case INDEX:
				mOutput.setPercentOutput(mConfig.bottomSparkIndexingOutput * breh);
				mBottomOutput.setPercentOutput(mConfig.bottomTalonIndexingOutput * multiplier);
				mBlockOutput = true;
				break;
			case WAITING_TO_FEED:
				mOutput.setIdle();
				mBottomOutput.setIdle();
				mBlockOutput = false;
				break;
			case FEED_SINGLE:
				mOutput.setPercentOutput(mConfig.feedingOutput);
				mBottomOutput.setPercentOutput(mConfig.feedingOutput);
				mBlockOutput = false;
				break;
			case FEED_ALL:
				mOutput.setPercentOutput(mConfig.bottomSparkIndexingOutput);
				mBottomOutput.setPercentOutput(mConfig.bottomTalonIndexingOutput);
				mBlockOutput = false;
				break;
		}
		mHopperOutput = commands.indexerWantedHopperState == HopperState.OPEN;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public ControllerOutput getBottomOutput() {
		return mBottomOutput;
	}

	public boolean getHopperOutput() {
		return mHopperOutput;
	}

	public boolean getBlockOutput() {
		return mBlockOutput;
	}

	public enum BeltState {
		IDLE, INDEX, WAITING_TO_FEED, FEED_SINGLE, FEED_ALL
	}

	public enum HopperState {
		OPEN, CLOSED
	}
}
