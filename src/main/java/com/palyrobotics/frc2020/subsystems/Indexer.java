package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Indexer extends Subsystem {

	public enum IndexerState {
		IDLE, MOVING
	}

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mOutput;

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		IndexerState mState = commands.indexerWantedState;
		switch (mState) {
			case IDLE:
				mOutput.setPercentOutput(0.0);
			case MOVING:
				mOutput.setTargetVelocityProfiled(mConfig.transferVelocity, mConfig.gains);
		}
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}
}
