package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.DualSolenoid;

public class Indexer extends SubsystemBase {

	public enum IndexerState {
		IDLE, INDEX, WAITING_TO_FEED, FEED
	}

	public enum IndexerUpDownState {
		DOWN, UP
	}

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private DualSolenoid.State mUpDownOutput = DualSolenoid.State.FORWARD;
	private boolean mBlockOutput = false;

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		IndexerState state = commands.indexerWantedState;
		IndexerUpDownState upDownState = commands.indexerWantedUpDownState;
		switch (state) {
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
			case FEED:
				mOutput.setPercentOutput(mConfig.feedingOutput);
				mBlockOutput = true;
		}

		switch (upDownState) {
			case DOWN:
				mUpDownOutput = DualSolenoid.State.FORWARD;
				break;
			case UP:
				mUpDownOutput = DualSolenoid.State.REVERSE;
				break;
		}
	}

	public ControllerOutput getHorizontalOutput() {
		return mOutput;
	}

	public DualSolenoid.State getUpDownOutput() {
		return mUpDownOutput;
	}

	public boolean getBlockOutput() {
		return mBlockOutput;
	}
}
