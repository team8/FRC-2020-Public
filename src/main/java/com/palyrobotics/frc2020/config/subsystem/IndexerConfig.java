package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class IndexerConfig extends SubsystemConfigBase {

	public double bottomSparkIndexingOutput, bottomTalonIndexingOutput, topSparkIndexingOutput, feedingOutput;

	public double ballInchTolerance;
	public int ballCountRequired;
}
