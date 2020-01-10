package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.AbstractConfig;

public class PortConstants extends AbstractConfig {

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