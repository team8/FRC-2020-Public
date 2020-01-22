package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.ConfigBase;

public class PortConstants extends ConfigBase {

	/**
	 * Climber
	 */
	public int vidarClimberHorizontalId;
	public int vidarClimberVerticalId;
	public int vidarClimberSolenoidId;

	/**
	 * Drivetrain
	 */
	public int vidarDriveLeftMasterId, vidarDriveLeftSlaveId;
	public int vidarDriveRightMasterId, vidarDriveRightSlaveId;

	/**
	 * Indexer
	 */
	public int vidarIndexerHorizontalId;
	public int vidarIndexerVerticalId;
	public int vidarIndexerExtendingSolenoidId, vidarIndexerRetractingSolenoidId;
	public int vidarIndexerBlockingSolenoidId;

	/**
	 * Intake
	 */
	public int vidarIntakeId;
	public int vidarIntakeExtendingSolenoidId, vidarIntakeRetractingSolenoidId;

	/**
	 * Shooter
	 */
	public int vidarShooterMasterId, vidarShooterSlaveId;
	public int vidarShooterExtendingSolenoidId, vidarShooterRetractingSolenoidId;
	public int vidarShooterBlockingSolenoidId;

	/**
	 * Spinner
	 */
	public int vidarSpinnerId;

}
