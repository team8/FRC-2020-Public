package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Indexer extends SubsystemBase {

	public enum IndexerState {
		IDLE, INDEX
	}

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mHorizontalOutput = new ControllerOutput(), mVerticalOutput = new ControllerOutput();

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
				mHorizontalOutput.setIdle();
				mVerticalOutput.setIdle();
				break;
			case INDEX:
				mHorizontalOutput.setTargetVelocityProfiled(mConfig.horizontalIntakeVelocity,
						mConfig.horizontalProfiledVelocityGains);
				mVerticalOutput.setTargetVelocityProfiled(mConfig.verticalIntakeVelocity,
						mConfig.verticalProfiledVelocityGains);
				break;
		}
	}

	public ControllerOutput getHorizontalOutput() {
		return mHorizontalOutput;
	}

	public ControllerOutput getVerticalOutput() {
		return mVerticalOutput;
	}
}
