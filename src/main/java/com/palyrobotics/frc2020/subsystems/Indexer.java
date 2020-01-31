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
		IDLE, HOPPER_OPEN, HOPPER_CLOSED, WAITING_TO_FEED, FEED
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
		switch (state) {
			case IDLE:
				mOutput.setIdle();
				mBlockOutput = false;
				break;
			case HOPPER_CLOSED:
				mOutput.setPercentOutput(mConfig.indexingOutput);
				mUpDownOutput = DualSolenoid.State.FORWARD;
				mBlockOutput = false;
				break;
			case HOPPER_OPEN:
				mOutput.setIdle();
				mUpDownOutput = DualSolenoid.State.REVERSE;
				mBlockOutput = false;
				break;
			case WAITING_TO_FEED:
				mOutput.setIdle();
				mBlockOutput = true;
				break;
			case FEED:
				mOutput.setPercentOutput(mConfig.feedingOutput);
				mBlockOutput = true;
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
