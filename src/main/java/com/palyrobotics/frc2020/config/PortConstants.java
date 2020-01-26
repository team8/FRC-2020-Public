package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.ConfigBase;

public class PortConstants extends ConfigBase {

	/**
	 * Climber
	 */
	public int nariClimberHorizontalId;
	public int nariClimberVerticalId;
	public int nariClimberSolenoidId;

	/**
	 * Drivetrain
	 */
	public int nariDriveLeftMasterId, nariDriveLeftSlaveId;
	public int nariDriveRightMasterId, nariDriveRightSlaveId;

	/**
	 * Indexer
	 */
	public int nariIndexerHorizontalId;
	public int nariIndexerVerticalId;
	public int nariIndexerExtendingSolenoidId, nariIndexerRetractingSolenoidId;
	public int nariIndexerBlockingSolenoidId;
	public int nariIndexerBackUltrasonicEcho, nariIndexerBackUltrasonicPing;
	public int nariIndexerFrontUltrasonicEcho, nariIndexerFrontUltrasonicPing;
	public int nariIndexerTopUltrasonicEcho, nariIndexerTopUltrasonicPing;

	/**
	 * Intake
	 */
	public int nariIntakeId;
	public int nariIntakeExtendingSolenoidId, nariIntakeRetractingSolenoidId;

	/**
	 * Shooter
	 */
	public int nariShooterMasterId, nariShooterSlaveId;
	public int nariShooterExtendingSolenoidId, nariShooterRetractingSolenoidId;
	public int nariShooterBlockingSolenoidId;

	/**
	 * Spinner
	 */
	public int nariSpinnerId;

}
