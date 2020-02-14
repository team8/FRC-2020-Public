package com.palyrobotics.frc2020.config.subsystem;

import com.palyrobotics.frc2020.util.config.SubsystemConfigBase;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class IndexerConfig extends SubsystemConfigBase {

	public double sparkIndexingOutput, talonIndexingOutput, feedingOutput, reverseTime;

	public double ballInchTolerance;
	public int ballCountRequired;
}
