package com.palyrobotics.frc2020.subsystems.controllers.indexer;

import com.palyrobotics.frc2020.config.constants.IndexerConstants;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;

public class FeedColumnController extends Indexer.IndexerColumnController {

	private Limelight mLimelight = new Limelight();
	private IndexerConfig mIndexerConfig = Configs.get(IndexerConfig.class);

	public FeedColumnController(RobotState state) {
		super(state);
	}

	@Override
	protected void update(RobotState state) {
		System.out.println("Running Feed Controller");

		double indexerOutput = IndexerConstants.kTargetDistanceToIndexerVelocity.getInterpolated(mLimelight.getEstimatedDistanceInches());
		mMasterSparkOutput.setTargetVelocity(indexerOutput, mIndexerConfig.masterSparkVelocityGains);
		mSlaveSparkOutput.setTargetVelocity(indexerOutput, mIndexerConfig.slaveSparkVelocityGains);
	}

	@Override
	protected boolean isFinished(RobotState state) {
		return true;
	}
}
