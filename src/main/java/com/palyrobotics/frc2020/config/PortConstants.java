package com.palyrobotics.frc2020.config;

import com.palyrobotics.frc2020.util.config.AbstractConfig;

public class PortConstants extends AbstractConfig {

    /**
     * Drivetrain
     */
    public int vidarLeftDriveMasterDeviceID, vidarLeftDriveSlave1DeviceID, vidarLeftDriveSlave2DeviceID;

    public int vidarRightDriveMasterDeviceID, vidarRightDriveSlave1DeviceID, vidarRightDriveSlave2DeviceID;

    /**
     * Spinner
     */
    public int spinnerTalonDeviceID;

    /**
     * Intake
     */
    public int vidarIntakeDeviceID;
    /**
     * Intake
     */

    /**
     * Shooter
     */
    public int shooterSparkMaxMasterDeviceID;
    public int shooterSparkMaxSlaveDeviceID;
    public int horizontalSolenoidDeviceID;
    public int verticalSolenoidDeviceID;



}