package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.util.config.AbstractConfig;

public class PortConstants extends AbstractConfig {
    
    // Drivetrain
    public int vidarLeftDriveMasterDeviceID, vidarLeftDriveSlave1DeviceID, vidarLeftDriveSlave2DeviceID;

    public int vidarRightDriveMasterDeviceID, vidarRightDriveSlave1DeviceID, vidarRightDriveSlave2DeviceID;

    // Elevator
    public int vidarElevatorMasterSparkID, vidarElevatorSlaveSparkID;

    public int vidarElevatorDoubleSolenoidForwardsID, vidarElevatorDoubleSolenoidReverseID, vidarElevatorHolderSolenoidID;

    // Intake
    public int vidarIntakeMasterDeviceID, vidarIntakeSlaveDeviceID, vidarIntakeTalonDeviceID, vidarAnalogPot;

    // Shooter
    public int vidarShooterMasterVictorDeviceID, vidarShooterSlaveVictorDeviceID;

    // Shovel
    public int vidarShovelDeviceID, vidarShovelSolenoidUpDownID, vidarShovelSolenoidUpDownID2;

    public int vidarShovelHFXPort, vidarShovelPDPPort;

    // Pusher
    public int vidarPusherSparkID, vidarPusherPotID;

    // Fingers
    public int vidarOpenCloseSolenoidForwardID, vidarOpenCloseSolenoidReverseID, vidarExpelSolenoidForwardID, vidarExpelSolenoidReverseID;

    /**
     * Ultrasonics
     */

    public int vidarIntakeLeftUltrasonicPing, vidarIntakeLeftUltrasonicEcho, vidarIntakeRightUltrasonicPing, vidarIntakeRightUltrasonicEcho;

    public int vidarPusherUltrasonicPing, vidarPusherUltrasonicEcho;
}