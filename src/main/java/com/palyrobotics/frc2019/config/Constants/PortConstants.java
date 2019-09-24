package com.palyrobotics.frc2019.config.Constants;

import com.palyrobotics.frc2019.util.configv2.AbstractConfig;

public class PortConstants extends AbstractConfig {
    // DRIVETRAIN
    // CAN BUS slots for drivetrain
    public int vidarLeftDriveMasterDeviceID, vidarLeftDriveSlave1DeviceID, vidarLeftDriveSlave2DeviceID;

    public int vidarRightDriveMasterDeviceID, vidarRightDriveSlave1DeviceID, vidarRightDriveSlave2DeviceID;

    // ELEVATOR
    public int vidarElevatorMasterSparkID, vidarElevatorSlaveSparkID;

    public int vidarElevatorDoubleSolenoidForwardsID, vidarElevatorDoubleSolenoidReverseID, vidarElevatorHolderSolenoidID;

    // INTAKE
    public int vidarIntakeMasterDeviceID, vidarIntakeSlaveDeviceID, vidarIntakeTalonDeviceID, vidarAnalogPot;

    // SHOOTER
    public int vidarShooterMasterVictorDeviceID, vidarShooterSlaveVictorDeviceID;

    // SHOVEL
    public int vidarShovelDeviceID, vidarShovelSolenoidUpDownID, vidarShovelSolenoidUpDownID2;

    public int vidarShovelHFXPort, vidarShovelPDPPort;

    // PUSHER
    public int vidarPusherSparkID, vidarPusherPotID;

    // FINGERS
    public int vidarOpenCloseSolenoidForwardID, vidarOpenCloseSolenoidReverseID, vidarExpelSolenoidForwardID, vidarExpelSolenoidReverseID;

    /**
     * Ultrasonics
     */

    public int vidarIntakeLeftUltrasonicPing, vidarIntakeLeftUltrasonicEcho, vidarIntakeRightUltrasonicPing, vidarIntakeRightUltrasonicEcho;

    public int vidarPusherUltrasonicPing, vidarPusherUltrasonicEcho;
}