package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.ConfigBase;

@SuppressWarnings ("java:S1104")
public class PortConstants extends ConfigBase {

	/**
	 * Climber
	 */
	public int nariClimberId;
	public int nariClimberSolenoidId;

	/**
	 * Drivetrain
	 */
	public int nariDriveLeftMasterId, nariDriveLeftSlaveId;
	public int nariDriveRightMasterId, nariDriveRightSlaveId;
	public int nariDriveGyroId;

	/**
	 * Indexer
	 */
	public int nariIndexerMasterId, nariIndexerSlaveId, nariIndexerLeftVTalonId, nariIndexerRightVTalonId;
	public int nariIndexerHopperSolenoidId;
	public int nariIndexerBlockingSolenoidId;
	public int nariIndexerBackInfraredDio, nariIndexerFrontInfraredDio, nariIndexerTopInfraredDio;

	/**
	 * Intake
	 */
	public int nariIntakeId;
	public int nariIntakeSolenoidId;

	/**
	 * Lighting
	 */
	public int nariLightingPwmPort;

	/**
	 * Shooter
	 */
	public int nariShooterMasterId, nariShooterSlaveId;
	public int nariShooterHoodSolenoid;
	public int nariShooterBlockingSolenoidId;

	/**
	 * Spinner
	 */
	public int nariSpinnerId;
}
