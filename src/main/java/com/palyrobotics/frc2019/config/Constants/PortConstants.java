package com.palyrobotics.frc2019.config.Constants;

public class PortConstants {
    //DRIVETRAIN
    //CAN BUS slots for drivetrain
    public static final int kVidarLeftDriveMasterDeviceID = 15;
    public static final int kVidarLeftDriveSlave1DeviceID = 14;
    public static final int kVidarLeftDriveSlave2DeviceID = 13;

    public static final int kVidarRightDriveMasterDeviceID = 0;
    public static final int kVidarRightDriveSlave1DeviceID = 1;
    public static final int kVidarRightDriveSlave2DeviceID = 2;

    //ELEVATOR
    public static final int kVidarElevatorMasterSparkID = 4;
    public static final int kVidarElevatorSlaveSparkID = 12;

    public static final int kVidarElevatorDoubleSolenoidForwardsID = 0;
    public static final int kVidarElevatorDoubleSolenoidReverseID = 0;
    public static final int kVidarElevatorHolderSolenoidID = 0;

    public static final int kElevatorHFXPort = 1;

    //INTAKE
    public static final int kVidarIntakeMasterDeviceID = 4;
    public static final int kVidarIntakeSlaveDeviceID = 5;
    public static final int kVidarIntakeVictorDeviceID = 6;
    public static final int kVidarAnalogPot = 1;

    //SHOOTER
    public static final int kVidarShooterMasterVictorDeviceID = 10;
    public static final int kVidarShooterSlaveVictorDeviceID = 7;

    //SHOVEL
    public static final int kVidarShovelDeviceID = 9;
    public static final int kVidarShovelSolenoidUpDownID = 0;
    public static final int kVidarShovelHFXPort = 1;
    public static final int kVidarShovelPDPPort = 0;

    //PUSHER
    public static final int kVidarPusherVictorID = 8;
    public static final int kVidarPusherPotID = 0;

    //FINGERS
    public static final int kVidarOpenCloseSolenoidForwardID = 0;
    public static final int kVidarOpenCloseSolenoidReverseID = 1;
    public static final int kVidarExpelSolenoidForwardID = 2;
    public static final int kVidarExpelSolenoidReverseID = 3;

    //PCM 0
    public static final int kVidarIntakeUpDownSolenoidForwardID = 2;
    public static final int kVidarIntakeUpDownSolenoidReverseID = 5;

    //PCM 1
    public static final int kInOutSolenoidA = 0;
    public static final int kInOutSolenoidB = 1;

    public static final int kGyroPort = 7;


    /**
     * Ultrasonics
     */

    public static final int kLeftUltrasonicPing = 0;
    public static final int kLeftUltrasonicEcho = 1;
    public static final int kRightUltrasonicPing = 2;
    public static final int kRightUltrasonicEcho = 3;
}
