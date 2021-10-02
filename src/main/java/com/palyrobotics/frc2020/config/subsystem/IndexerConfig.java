package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2020.util.control.Gains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class IndexerConfig extends SubsystemConfigBase {

	public int columnStallCurrentLimit, columnFreeCurrentLimit, vTalonCurrentLimit;
	public double rampRate, masterSparkIndexDistance, slaveSparkIndexDistance, masterSparkUnIndexDistance, slaveSparkUnIndexDistance,
			masterSparkReverseFeedPo, slaveSparkReverseFeedPo, rightVTalonPo, leftVTalonPo, rightVTalonSlowerPo, indexFinishedMinThreshold,
			unIndexFinishedMinThreshold, maximumIndexerColumnPo, indexControllerTimeoutSec;
	public boolean blockingSolenoidExtended, hopperSolenoidExtended;
	public Gains masterSparkPositionGains, masterSparkVelocityGains, slaveSparkPositionGains, slaveSparkVelocityGains;
}
