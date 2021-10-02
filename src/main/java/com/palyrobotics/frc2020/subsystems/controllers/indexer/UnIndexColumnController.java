package com.palyrobotics.frc2020.subsystems.controllers.indexer;

import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Indexer;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpiutil.math.MathUtil;

public class UnIndexColumnController extends Indexer.IndexerColumnController {

	private final PIDController mMasterSparkPIDController = new PIDController(mConfig.masterSparkPositionGains.p, mConfig.masterSparkPositionGains.i, mConfig.masterSparkPositionGains.d),
			mSlaveSparkPIDController = new PIDController(mConfig.slaveSparkPositionGains.p, mConfig.slaveSparkPositionGains.i, mConfig.slaveSparkPositionGains.d);
	private final double mMasterSparkEncWantedPosition, mSlaveSparkEncWantedPosition, mStartTime;

	public UnIndexColumnController(RobotState state) {
		super(state);
		mMasterSparkEncWantedPosition = state.indexerMasterEncPosition - mConfig.masterSparkUnIndexDistance;
		mSlaveSparkEncWantedPosition = state.indexerSlaveEncPosition - mConfig.slaveSparkUnIndexDistance;
		mStartTime = state.gameTime;
	}

	@Override
	protected void update(RobotState state) {
		System.out.println("Running UnIndex Controller");
		mMasterSparkPIDController.setPID(mConfig.masterSparkPositionGains.p, mConfig.masterSparkPositionGains.i, mConfig.masterSparkPositionGains.d);
		mSlaveSparkPIDController.setPID(mConfig.slaveSparkPositionGains.p, mConfig.slaveSparkPositionGains.i, mConfig.slaveSparkPositionGains.d);

		mMasterSparkOutput.setPercentOutput(MathUtil.clamp(mMasterSparkPIDController.calculate(state.indexerMasterEncPosition, mMasterSparkEncWantedPosition), -mConfig.maximumIndexerColumnPo, mConfig.maximumIndexerColumnPo));
		mSlaveSparkOutput.setPercentOutput(MathUtil.clamp(mSlaveSparkPIDController.calculate(state.indexerSlaveEncPosition, mSlaveSparkEncWantedPosition), -mConfig.maximumIndexerColumnPo, mConfig.maximumIndexerColumnPo));

		LiveGraph.add("MasterSparkPo", MathUtil.clamp(mMasterSparkPIDController.calculate(state.indexerMasterEncPosition, mMasterSparkEncWantedPosition), -mConfig.maximumIndexerColumnPo, mConfig.maximumIndexerColumnPo));
		LiveGraph.add("MasterTarget", mMasterSparkEncWantedPosition);
		LiveGraph.add("SlaveTarget", mSlaveSparkEncWantedPosition);
		LiveGraph.add("isFinished", Math.abs(mMasterSparkEncWantedPosition - state.indexerMasterEncPosition));
	}

	@Override
	protected boolean isFinished(RobotState state) {
		return (Math.abs(mMasterSparkEncWantedPosition - state.indexerMasterEncPosition) < mConfig.unIndexFinishedMinThreshold && Math.abs(mSlaveSparkEncWantedPosition - state.indexerSlaveEncPosition) < mConfig.unIndexFinishedMinThreshold) || (state.gameTime - mStartTime) > mConfig.indexControllerTimeoutSec;
	}
}
