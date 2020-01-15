package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.ConfigBase;

public class PortConstants extends ConfigBase {

	/**
	 * Drivetrain
	 */
	public int vidarLeftDriveMasterDeviceId, vidarLeftDriveSlave1DeviceId, vidarLeftDriveSlave2DeviceId;

	public int vidarRightDriveMasterDeviceId, vidarRightDriveSlave1DeviceId, vidarRightDriveSlave2DeviceId;

	/**
	 * Spinner
	 */
	public int spinnerTalonDeviceId;

	/**
	 * Intake
	 */
	public int vidarIntakeDeviceId;
}
